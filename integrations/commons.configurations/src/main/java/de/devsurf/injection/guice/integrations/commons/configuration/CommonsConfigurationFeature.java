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
package de.devsurf.injection.guice.integrations.commons.configuration;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Singleton;

import de.devsurf.injection.guice.install.BindingStage;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;

import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.scanner.features.BindingScannerFeature;

/**
 * This Class will be called for each Class, which is annotated with
 * {@link Configuration} and which needs an Apache Commons-based Configuration.
 * 
 * @author Daniel Manzke
 * 
 */
@Singleton
public class CommonsConfigurationFeature extends BindingScannerFeature {
	private Logger _logger = Logger.getLogger(CommonsConfigurationFeature.class.getName());

	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		if (annotations.containsKey(Configuration.class.getName())) {
			Configuration config = (Configuration) annotations.get(Configuration.class.getName());
			if (FileConfiguration.class.isAssignableFrom(config.to())) {
				return BindingStage.BOOT_BEFORE;
			}
		}
		return BindingStage.IGNORE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		Configuration config = (Configuration) annotations.get(Configuration.class.getName());
		Named name = config.name();

		// TODO Implement Location overriding
		URL url;
		switch (config.location().type()) {
		case FILE:
			File file = new File(config.location().value());
			if (!file.exists()) {
				_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in "
						+ config.location() + ". In the Path " + file.getAbsolutePath()
						+ " no Configuration was found.");
				return;
			}
			try {
				url = file.toURI().toURL();
			} catch (MalformedURLException e) {
				_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in "
						+ config.location() + ". It has an illegal URL-Format.", e);
				return;
			}
			break;
		case URL:
			try {
				url = new URL(config.location().value());
			} catch (MalformedURLException e) {
				_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in "
						+ config.location() + ". It has an illegal URL-Format.", e);
				return;
			}
			break;
		case CLASSPATH:
		default:
			url = this.getClass().getResource(config.location().value());
			break;
		}

		if (url == null) {
			_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in "
					+ config.location() + ", because is couldn't be found in the Classpath.");
			// TODO Throw an exception if config doesn't exist?
			return;
		}

		Named named = null;
		if (name.value().length() > 0) {
			named = name;
		}

		FileConfiguration configuration;
		try {
			// Class<? extends FileConfiguration> interf =
			// config.to().asSubclass(FileConfiguration.class);
			Class<FileConfiguration> interf = (Class<FileConfiguration>) config.to();
			configuration = (FileConfiguration) injector.getInstance(interf);
			configuration.load(url);

			bindInstance(configuration, interf, named, null);
			bindInstance(configuration, FileConfiguration.class, named, null);
			bindInstance(configuration, org.apache.commons.configuration.Configuration.class,
				named, null);
		} catch (ConfigurationException e) {
			_logger.log(Level.WARNING, "Configuration " + name + " couldn't be loaded/bound: "
					+ e.getMessage(), e);
		}
	}
}