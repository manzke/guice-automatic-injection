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
package de.devsurf.injection.guice.configuration;

import java.net.URL;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import com.googlecode.rocoto.configuration.ConfigurationReader;
import com.googlecode.rocoto.configuration.readers.PropertiesURLReader;

public class PropertiesReader implements ConfigurationReader {
	private PropertiesURLReader reader;

	public PropertiesReader(URL url, boolean isXML) {
		reader = new PropertiesURLReader(url, isXML);
	}
	
	public Properties readNative() throws Exception{
		Properties properties = new Properties();
		
		Iterator<Entry<String, String>> configuration = reader.readConfiguration();
		for(Entry<String, String> entry;configuration.hasNext();){
			entry = configuration.next();
			properties.setProperty(entry.getKey(), entry.getValue());
		}
		
		return properties;
	}

	@Override
	public Iterator<Entry<String, String>> readConfiguration() throws Exception {
		return reader.readConfiguration();
	}
}
