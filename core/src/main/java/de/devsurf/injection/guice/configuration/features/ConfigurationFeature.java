/**
 * Copyright (C) 2010 Daniel Manzke <daniel.manzke@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package de.devsurf.injection.guice.configuration.features;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.googlecode.rocoto.configuration.readers.PropertiesURLReader;

import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.configuration.Configuration.Type;
import de.devsurf.injection.guice.configuration.ConfigurationModule;
import de.devsurf.injection.guice.configuration.PathConfig;
import de.devsurf.injection.guice.configuration.PropertiesProvider;
import de.devsurf.injection.guice.configuration.PropertiesReader;
import de.devsurf.injection.guice.install.BindingStage;
import de.devsurf.injection.guice.install.bindjob.BindingJob;
import de.devsurf.injection.guice.install.bindjob.ConfigurationBindingJob;
import de.devsurf.injection.guice.scanner.features.BindingScannerFeature;

/**
 * This class will bind a Properties-Instance or -Provider for each Class
 * annotated with {@link Configuration}.
 * 
 * @author Daniel Manzke
 * 
 */
@Singleton
public class ConfigurationFeature extends BindingScannerFeature {
	Logger _logger = Logger.getLogger(ConfigurationFeature.class.getName());

	@Inject
	private ConfigurationModule module;

	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		if (annotations.containsKey(Configuration.class.getName())) {
			Configuration config = (Configuration) annotations.get(Configuration.class.getName());
			if (Properties.class.isAssignableFrom(config.to())) {
				return BindingStage.BOOT_BEFORE;
			}
		}
		return BindingStage.IGNORE;
	}

	@Override
	public void process(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		Configuration config = (Configuration) annotations.get(Configuration.class.getName());
		Named name = config.name();

		URL url = null;
		if (config.alternative().value().length() > 0) {
			url = findURL(name, config.alternative());
			if (url != null) {
				try {
					//TODO Use an Executor to test, if the Stream can be opened?
					//FIXME What happens if Error Page is returned?
					/*
					final URL alternativeURL = url;
					Future<URL> submit = Executors.newSingleThreadExecutor().submit(new Callable<URL>() {
						@Override
						public URL call() throws Exception {
							alternativeURL.openConnection().getInputStream();
							return alternativeURL;
						}
					});
					submit.get(5, TimeUnit.SECONDS);
					*/
					url.openStream();
				} catch (Exception e) {
					url = null;
				}
			}
		}

		if (url == null) {
			url = findURL(name, config.location());
		}

		if (url == null) {
			_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in "
					+ config.location() + ", because is couldn't be found in the Classpath.");
			// TODO Throw an exception if config doesn't exist?
			return;
		}

		if (config.type() == Type.VALUES || config.type() == Type.BOTH) {
			BindingJob job = new ConfigurationBindingJob(config.name(), url.toString());
			if (!tracer.contains(job)) {
				/* && !(url.toString().startsWith("jar:")) */
				_logger.log(Level.INFO, "Trying to bind \"" + url.toString()
						+ "\" to rocoto Module.");
				module.addConfigurationReader(new PropertiesURLReader(url, url.toString().endsWith(".xml")));
				//TODO do we need protocol handling? file:/, ...
				tracer.add(job);
			}
		}

		if (config.type() == Type.CONFIGURATION || config.type() == Type.BOTH) {
			boolean isXML;
			String path = url.toString();
			if (path.endsWith(".xml")) {
				isXML = true;
			} else if (path.endsWith(".properties")) {
				isXML = false;
			} else {
				_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in " + path
						+ ", because is doesn't end with .xml or .properties.");
				// TODO Throw an exception if config has another format?
				return;
			}

			Named named = null;
			if (name.value().length() > 0) {
				named = name;
			}

			if (!config.lazy()) {
				Properties properties;
				try {
					properties = new PropertiesReader(url, isXML).readNative();
				} catch (Exception e) {
					_logger.log(Level.WARNING, "Configuration " + name + " in " + url
							+ ", couldn't be loaded: " + e.getMessage(), e);
					return;
				}

				bindInstance(properties, Properties.class, named, null);
			} else {
				Provider<Properties> provider = new PropertiesProvider(url, isXML);
				bindProvider(provider, Properties.class, named, Singleton.class);
			}
		}
	}

	private URL findURL(Named name, PathConfig config) {
		URL url = null;
		String path = resolver.resolve(config.value());

		switch (config.type()) {
		case FILE:
			File file = new File(path);
			if (!file.exists()) {
				_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in " + path
						+ ". In the Path " + file.getAbsolutePath()
						+ " no Configuration was found.");
				return null;
			}
			if (file.isFile()) {
				try {
					url = file.toURI().toURL();
				} catch (MalformedURLException e) {
					_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in " + path
							+ ". It has an illegal URL-Format.", e);
					return null;
				}
			} /*
			   * else if (file.isDirectory()) { for (File entry :
			   * file.listFiles()) { try { url = entry.toURI().toURL(); } catch
			   * (MalformedURLException e) { _logger.log(Level.WARNING,
			   * "Ignoring Configuration " + name + " in " + path +
			   * ". It has an illegal URL-Format.", e); return null; } } }
			   */

			break;
		case URL:
			try {
				url = new URL(path);
			} catch (MalformedURLException e) {
				_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in " + path
						+ ". It has an illegal URL-Format.", e);
				return null;
			}
			break;
		case CLASSPATH:
		default:
			url = this.getClass().getResource(path);
			break;
		}

		return url;
	}
}