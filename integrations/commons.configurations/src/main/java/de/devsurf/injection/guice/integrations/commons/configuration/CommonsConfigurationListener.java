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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.scanner.GuiceAnnotationListener;
import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;

@Singleton
public class CommonsConfigurationListener extends GuiceAnnotationListener {
    private Logger _logger = Logger.getLogger(CommonsConfigurationListener.class.getName());

    @Override
    public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
	if (annotations.containsKey(Configuration.class.getName())) {
	    Configuration config = (Configuration) annotations.get(Configuration.class.getName());
	    if (FileConfiguration.class.isAssignableFrom(config.bind())) {
		return BindingStage.BOOT_BEFORE;
	    }
	}
	return BindingStage.IGNORE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
	Configuration config = (Configuration) annotations.get(Configuration.class.getName());
	String name = config.name();

	URL url;
	switch (config.pathType()) {
	case FILE:
	    File file = new File(config.path());
	    if (!file.exists()) {
		_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in "
			+ config.path() + ". In the Path " + file.getAbsolutePath()
			+ " no Configuration was found.");
		return;
	    }
	    try {
		url = file.toURI().toURL();
	    } catch (MalformedURLException e) {
		_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in "
			+ config.path() + ". It has an illegal URL-Format.", e);
		return;
	    }
	    break;
	case URL:
	    try {
		url = new URL(config.path());
	    } catch (MalformedURLException e) {
		_logger.log(Level.WARNING, "Ignoring Configuration " + name + " in "
			+ config.path() + ". It has an illegal URL-Format.", e);
		return;
	    }
	    break;
	case CLASSPATH:
	default:
	    url = this.getClass().getResource(config.path());
	    break;
	}

	if (url == null) {
	    _logger.log(Level.WARNING, "Ignoring Configuration " + name + " in " + config.path()
		    + ", because is couldn't be found in the Classpath.");
	    // TODO Throw an exception if config doesn't exist?
	    return;
	}

	Named named = null;
	if(name.length() > 0){
	    named = Names.named(name);
	}
	
	FileConfiguration configuration;
	try {
	    Class<FileConfiguration> interf = (Class<FileConfiguration>) config.bind();
	    configuration = injector.getInstance(interf);
	    configuration.load(url);
		
	    bindInstance(configuration, config.bind(), named, null);
	    bindInstance(configuration, org.apache.commons.configuration.Configuration.class, named, null);
	} catch (ConfigurationException e) {
	    e.printStackTrace();
	}
    }
}