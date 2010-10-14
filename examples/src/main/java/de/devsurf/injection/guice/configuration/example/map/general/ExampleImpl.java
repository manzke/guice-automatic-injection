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
package de.devsurf.injection.guice.configuration.example.map.general;

import java.util.Properties;

import com.google.inject.Inject;

import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;

/**
 * This class implements the Example interface and is not annotated like the
 * other Examples, due the fact, that the {@link ExampleModule} will bind it
 * manually. In this Example the {@link ASMClasspathScanner} is used, to find
 * the {@link ExampleModule} and automatically install it.
 * 
 * @author Daniel Manzke
 * 
 */
public class ExampleImpl implements Example {
	@Inject
	private Properties config;

	@Override
	public String sayHello() {
		return "sayHello() - " + config.getProperty("message");
	}
}
