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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;

import javax.inject.Named;

/**
 * Use this Annotation to express your need, that a Configuration should be
 * loaded, so it can be bound to an Object.
 * 
 * @author Daniel Manzke
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {
	/**
	 * Name the Configuration should be bound to.
	 * 
	 * @return Name the Configuration should be bound to.
	 */
	Named name() default @Named("");

	/**
	 * Path/URL where the Configuration could be found.
	 * 
	 * @return Path/URL where the Configuration could be found.
	 */
	PathConfig location();

	PathConfig alternative() default @PathConfig("");

	/**
	 * Class/Interface where the Configuration should be bound to.
	 * 
	 * @return Class/Interface where the Configuration should be bound to.
	 */
	Class<? extends Object> to() default Properties.class;

	/**
	 * This does only make sense if you are using the Provider Interface.
	 * 
	 * public class Service{
	 * 
	 * @Inject Provider<Properties> properties;
	 * 
	 *         public void do(){ Properties properties = properties.get(); } }
	 * 
	 * @return true if the Configuration should be load lazy
	 */
	boolean lazy() default false;

	/**
	 * Specify what should be bound for the Configuration
	 * 
	 * Configuration -> Configuration only Values -> Named Bindings for the
	 * Values Both -> Configuration and Named Bindings
	 * 
	 * @return
	 */
	Type type() default Type.CONFIGURATION;

	public enum Type {
		/**
		 * Binds only the Configuration itself.
		 */
		CONFIGURATION,
		/**
		 * Named Bindings for the Key/Values (only possible if not lazy)
		 */
		VALUES,
		/**
		 * The Configurationd and Named Bindings will be done. (only possible if
		 * not lazy)
		 */
		BOTH
	}
}
