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
package de.devsurf.injection.guice;

import java.lang.annotation.Annotation;

import com.google.inject.name.Named;

/**
 * Interoperability class, so we can support JSR330- and Guice-Named-Annotation.
 * 
 * @author Daniel Manzke
 * 
 */
public class NamedInterop {
	public static String getName(Annotation annotation) {
		if (annotation instanceof Named) {
			return ((Named) annotation).value();
		} else if (annotation instanceof javax.inject.Named) {
			return ((javax.inject.Named) annotation).value();
		}
		return "";
	}
}
