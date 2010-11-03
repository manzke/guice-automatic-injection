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
package de.devsurf.injection.guice.aop.example.interceptor;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.aop.Intercept;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;

/**
 * This class implements the Example interface and uses the {@link Bind}-
 * Annotation, so it will be recognized by the {@link ClasspathScanner}. In this
 * Example the {@link ASMClasspathScanner} is used.
 * 
 * @author Daniel Manzke
 * 
 */
@Bind
public class ExampleImpl implements Example {
	@Override
	@Intercept
	public String sayHello() {
		return "yeahhh!!!";
	}

	@Override
	@Intercept
	public String convert(String message, boolean enabled, int times) {
		if (enabled) {
			String part = message;
			for (int i = 0; i < times; i++) {
				message += part;
			}
		}
		return message;
	}
}
