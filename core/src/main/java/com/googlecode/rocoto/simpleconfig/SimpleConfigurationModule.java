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
package com.googlecode.rocoto.simpleconfig;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;

/**
 * Simple configuration module to make easier the configuration properties to
 * Google Guice binder.
 * 
 * @author Simone Tripodi
 * @version $Id: SimpleConfigurationModule.java 491 2010-10-01 13:16:22Z
 *          simone.tripodi $
 */
public class SimpleConfigurationModule extends AbstractModule {

	/**
	 * The environment variable prefix, {@code env.}
	 */
	private static final String ENV_PREFIX = "env.";

	/**
	 * This class logger.
	 */
	private final Log log = LogFactory.getLog(this.getClass());

	/**
	 * This class loader.
	 */
	private final ClassLoader defaultClassLoader = this.getClass().getClassLoader();

	/**
	 * The default file filter to traverse properties dirs.
	 */
	private final AbstractPropertiesFileFilter defaultFileFilter = new DefaultPropertiesFileFilter();

	/**
	 * The list of properties have to be read.
	 */
	protected List<PropertiesReader> readers = new ArrayList<PropertiesReader>();

	/**
	 * Adds {@link Properties} to the Guice Binder by loading a classpath
	 * resource file, using the default {@code ClassLoader}.
	 * 
	 * @param classpathResource
	 *            the classpath resource file.
	 */
	public SimpleConfigurationModule addProperties(String classpathResource) {
		return this.addProperties(classpathResource, this.defaultClassLoader);
	}

	/**
	 * Adds {@link Properties} to the Guice Binder by loading a classpath
	 * resource file, using the user specified {@code ClassLoader}.
	 * 
	 * @param classpathResource
	 *            the classpath resource file.
	 * @param classLoader
	 *            the user specified {@code ClassLoader}.
	 */
	public SimpleConfigurationModule addProperties(String classpathResource, ClassLoader classLoader) {
		return this.addProperties(classpathResource, classLoader, false);
	}

	/**
	 * Adds XML {@link Properties} to the Guice Binder by loading a classpath
	 * resource file, using the default {@code ClassLoader}.
	 * 
	 * @param classpathResource
	 *            the classpath resource file.
	 */
	public SimpleConfigurationModule addXMLProperties(String classpathResource) {
		return this.addXMLProperties(classpathResource, this.defaultClassLoader);
	}

	/**
	 * Adds XML {@link Properties} to the Guice Binder by loading a classpath
	 * resource file, using the user specified {@code ClassLoader}.
	 * 
	 * @param classpathResource
	 *            the classpath resource file.
	 * @param classLoader
	 *            the user specified {@code ClassLoader}.
	 */
	public SimpleConfigurationModule addXMLProperties(String classpathResource,
			ClassLoader classLoader) {
		return this.addProperties(classpathResource, this.defaultClassLoader, true);
	}

	/**
	 * 
	 * @param classpathResource
	 * @param classLoader
	 * @param isXML
	 */
	private SimpleConfigurationModule addProperties(String classpathResource,
			ClassLoader classLoader, boolean isXML) {
		return this.addPropertiesReader(new URLPropertiesReader(classpathResource, classLoader,
			isXML));
	}

	/**
	 * Adds {@link Properties} to the Guice Binder by loading a file; if the
	 * user specified file is a directory, it will be traversed and every file
	 * that matches with {@code *.properties} and {@code *.xml} patterns will be
	 * load as properties file.
	 * 
	 * @param configurationFile
	 *            the properties file or the root dir has to be traversed.
	 */
	public SimpleConfigurationModule addProperties(File configurationFile) {
		return this.addProperties(configurationFile, this.defaultFileFilter);
	}

	/**
	 * Adds {@link Properties} to the Guice Binder by loading a file; if the
	 * user specified file is a directory, it will be traversed and every file
	 * that matches with user specified patterns will be load as properties
	 * file.
	 * 
	 * @param configurationFile
	 *            the properties file or the root dir has to be traversed.
	 * @param filter
	 *            the user specified properties file patterns.
	 */
	public SimpleConfigurationModule addProperties(File configurationFile,
			AbstractPropertiesFileFilter filter) {
		if (configurationFile == null) {
			throw new IllegalArgumentException("'configurationFile' argument can't be null");
		}
		if (filter == null) {
			throw new IllegalArgumentException("'filter' argument can't be null");
		}

		if (!configurationFile.exists()) {
			throw new RuntimeException("Impossible to load properties file '" + configurationFile
					+ " because it doesn't exist");
		}

		if (configurationFile.isDirectory()) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Configuration file '" + configurationFile.getAbsolutePath()
						+ "' is a directory, traversing it to look for properties file");
			}
			File[] childs = configurationFile.listFiles(filter);
			if (childs == null || childs.length == 0) {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Configuration directory file '"
							+ configurationFile.getAbsolutePath() + "' is empty");
				}
				return this;
			}
			for (File file : childs) {
				this.addProperties(file, filter);
			}
			return this;
		}

		return this.addPropertiesReader(new URLPropertiesReader(configurationFile, filter
			.isXMLProperties(configurationFile)));
	}

	/**
	 * Adds {@link Properties} to the Guice Binder by loading data from a URL.
	 * 
	 * @param configurationUrl
	 *            the properties URL.
	 */
	public SimpleConfigurationModule addProperties(URL configurationUrl) {
		return this.addProperties(configurationUrl, false);
	}

	/**
	 * Adds XML {@link Properties} to the Guice Binder by loading data from a
	 * URL.
	 * 
	 * @param configurationUrl
	 *            the properties URL.
	 */
	public SimpleConfigurationModule addXMLProperties(URL configurationUrl) {
		return this.addProperties(configurationUrl, true);
	}

	/**
	 * 
	 * @param configurationUrl
	 * @param isXML
	 */
	private final SimpleConfigurationModule addProperties(URL configurationUrl, boolean isXML) {
		return this.addPropertiesReader(new URLPropertiesReader(configurationUrl, isXML));
	}

	/**
	 * Adds Java System properties to the Guice Binder.
	 */
	public SimpleConfigurationModule addSystemProperties() {
		return this.addProperties(System.getProperties());
	}

	/**
	 * Adds environment variables, prefixed with {@code env.}, to the Guice
	 * Binder.
	 */
	public SimpleConfigurationModule addEnvironmentVariables() {
		return this.addPropertiesReader(new DefaultPropertiesReader(ENV_PREFIX, System.getenv()));
	}

	/**
	 * 
	 * @param properties
	 * @return
	 * @since 3.2
	 */
	public SimpleConfigurationModule addProperties(Map<?, ?> properties) {
		if (properties == null) {
			throw new IllegalArgumentException("Parameter 'properties' must be not null");
		}
		return this.addPropertiesReader(new DefaultPropertiesReader(properties));
	}

	/**
	 * 
	 * @param propertiesReader
	 * @return
	 * @since 3.2
	 */
	public SimpleConfigurationModule addPropertiesReader(PropertiesReader propertiesReader) {
		if (propertiesReader == null) {
			throw new IllegalArgumentException("Parametr 'propertiesReader' must not be null");
		}
		this.readers.add(propertiesReader);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {
		for (PropertiesReader reader : this.readers) {
			try {
				Iterator<Entry<String, String>> properties = reader.read();
				while (properties.hasNext()) {
					Entry<String, String> property = properties.next();
					LinkedBindingBuilder<String> bindingBuilder = this.bind(Key.get(String.class,
						Names.named(property.getKey())));

					Formatter formatter = new Formatter(property.getValue());
					if (formatter.containsKeys()) {
						bindingBuilder.toProvider(formatter);
					} else {
						bindingBuilder.toInstance(property.getValue());
					}
				}
			} catch (Exception e) {
				this.addError(e);
			}
		}
	}

}
