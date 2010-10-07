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
package de.devsurf.injection.guice.integrations.rocoto;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.integrations.rocoto.ConfigurationModuleInstaller.ExtendedConfigurationModule;
import de.devsurf.injection.guice.scanner.ScannerFeature;

/**
 * Adds all URLs of Configurations to the Rocoto-Configuration Module.
 * 
 * @author Daniel Manzke
 * 
 */
@Singleton
public class RocotoConfigurationFeature implements ScannerFeature {
    private Logger _logger = Logger.getLogger(RocotoConfigurationFeature.class.getName());
    @Inject
    private ExtendedConfigurationModule module;

    @Override
    public void found(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
	if (annotations.containsKey(Configuration.class.getName())) {
	    Configuration config = (Configuration) annotations.get(Configuration.class.getName());
	    //TODO Implement location overriding
	    URL url;
	    switch (config.path().type()) {
	    case FILE:
		module.addProperties(new File(config.path().location()));
		break;
	    case URL:
		try {
		    url = new URL(config.path().location());
		} catch (MalformedURLException e) {
		    _logger.log(Level.WARNING, "Ignoring Configuration " + config.name() + " in "
			    + config.path() + ". It has an illegal URL-Format.", e);
		    return;
		}
		if (config.path().location().endsWith(".xml")) {
		    module.addXMLProperties(url);
		} else if (config.path().location().endsWith(".properties")) {
		    module.addProperties(url);
		} else {
		    _logger.log(Level.WARNING, "Ignoring Configuration " + config.name() + " in "
			    + config.path() + ", because is doesn't end with .xml or .properties.");
		}
		break;
	    case CLASSPATH:
	    default:
		url = this.getClass().getResource(config.path().location());
		if (url != null) {
		    try {
			module.addProperties(new File(url.toURI()));
		    } catch (URISyntaxException e) {
			_logger.log(Level.WARNING, "Ignoring Configuration " + config.name()
				+ " in " + config.path() + ". It has an illegal URL-Format.", e);
		    }
		} else {
		    _logger.log(Level.WARNING, "Ignoring Configuration " + config.name() + " in "
			    + config.path() + ", because is couldn't be found in the Classpath.");
		}

		break;
	    }
	}
    }
}