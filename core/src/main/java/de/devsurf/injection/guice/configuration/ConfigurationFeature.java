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
package de.devsurf.injection.guice.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.googlecode.rocoto.simpleconfig.Formatter;

import de.devsurf.injection.guice.configuration.Configuration.BindType;
import de.devsurf.injection.guice.configuration.rocoto.ConfigurationModule;
import de.devsurf.injection.guice.scanner.BindingScannerFeature;
import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;

/**
 * This class will bind a Properties-Instance or -Provider for each Class
 * annotated with {@link Configuration}.
 * 
 * @author Daniel Manzke
 * 
 */
@Singleton
public class ConfigurationFeature extends BindingScannerFeature {
    private Logger _logger = Logger.getLogger(ConfigurationFeature.class.getName());

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
	if (config.override().path().length() > 0) {
	    url = findURL(name, config.override());
	    if (url != null) {
		try {
		    url.openConnection().getInputStream();
		} catch (IOException e) {
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

	if (config.bindType() == BindType.VALUES || config.bindType() == BindType.BOTH) {
	    module.addProperties(url);
	}

	if (config.bindType() == BindType.CONFIGURATION || config.bindType() == BindType.BOTH) {
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
		    properties = read(url, isXML);
		} catch (IOException e) {
		    _logger.log(Level.WARNING, "Configuration " + name + " in " + url
			    + ", couldn't be loaded: " + e.getMessage(), e);
		    return;
		}

		bindInstance(properties, Properties.class, named, null);
	    } else {
		Provider<Properties> provider = new PropertiesProvider(url, isXML);
		bindProvider(provider, Properties.class, named, Scopes.SINGLETON);
	    }
	}
    }

    private URL findURL(Named name, PathConfig config) {
	URL url = null;
	String path = config.path();

	Formatter formatter = new Formatter(path);
	if (formatter.containsKeys()) {
	    formatter.setInjector(injector);
	    path = formatter.get();
	}

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
	    } /*else if (file.isDirectory()) {
		for (File entry : file.listFiles()) {
		    try {
			url = entry.toURI().toURL();
		    } catch (MalformedURLException e) {
			_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in " + path
				+ ". It has an illegal URL-Format.", e);
			return null;
		    }
		}
	    }*/

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

    private class PropertiesProvider implements Provider<Properties> {
	private URL url;
	private boolean isXML;

	public PropertiesProvider(URL url, boolean isXML) {
	    super();
	    this.url = url;
	    this.isXML = isXML;
	}

	@Override
	public Properties get() {
	    try {
		_logger.info("Doing lazy Loading for Configuration " + url);
		return read(url, isXML);
	    } catch (IOException e) {
		_logger.log(Level.WARNING, "Configuration in " + url + " couldn't be read.", e);
		return new Properties();
	    }
	}
    }


    private Properties read(URL url, boolean isXML) throws IOException {
	URLConnection connection = null;
	InputStream input = null;
	try {
	    connection = url.openConnection();
	    connection.setUseCaches(false);
	    input = connection.getInputStream();

	    Properties properties = new Properties();
	    if (isXML) {
		properties.loadFromXML(input);
	    } else {
		properties.load(input);
	    }
	    return properties;
	} finally {
	    if (connection != null && (connection instanceof HttpURLConnection)) {
		((HttpURLConnection) connection).disconnect();
	    }
	    if (input != null) {
		try {
		    input.close();
		} catch (IOException e) {
		    // close quietly
		}
	    }
	}
    }
}