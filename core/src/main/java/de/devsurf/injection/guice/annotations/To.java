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
package de.devsurf.injection.guice.annotations;

public @interface To {
	Type value() default Type.INTERFACES;

	Class<? extends Object>[] customs() default {};

	public static enum Type {
		/**
		 * Binds the Implementation to itself. Equals
		 * binder.bind(Implementation.class);
		 */
		IMPLEMENTATION,
		/**
		 * Binds the Implementation to all implemented Interfaces.
		 * 
		 * Equals: for(Interface interface: implementedInterfaces)
		 * binder.bind(interface).to(implementation);
		 */
		INTERFACES,
		/**
		 * Binds the Implementation to the extended Super-Class. Equals:
		 * binder.bind(superclass).to(implementation);
		 */
		SUPER,
		/**
		 * Binds the Implementation to the Classes specifed by @To(to={})
		 * 
		 * Equals: for(Class<?> class: toClasses)
		 * binder.bind(class).to(implementation);
		 */
		CUSTOM
	}
}