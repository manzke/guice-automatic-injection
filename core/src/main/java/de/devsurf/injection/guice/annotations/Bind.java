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
package de.devsurf.injection.guice.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Named;
import javax.inject.Qualifier;


/**
 * Annotate a Class which should be binded automatically. The Classpath Scanner,
 * will check for these classes. If the name()-Attribute is set (default is ""),
 * the class will be bound to the implemented interfaces and a named annotation.
 * 
 * You can overwrite the interfaces, which should be used for binding the class.
 * If bind()-Attribute is not set, the implemented interfaces will be used. If
 * set they will be ignored and overwritten.
 * 
 * If you annotate your class with {@link com.google.inject.Singleton} or
 * {@link javax.inject.Singleton} they will be also bound to the
 * Singleton-Scope.
 * 
 * @author Daniel Manzke
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@GuiceAnnotation
@Target( { ElementType.TYPE })
public @interface Bind {
	Named value() default @Named("");

	boolean multiple() default false;

	To to() default @To();
}
