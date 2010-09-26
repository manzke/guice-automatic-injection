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
    String name() default "";

    /**
     * Path/URL where the Configuration could be found.
     * 
     * @return Path/URL where the Configuration could be found.
     */
    String path() default "/common.properties";

    /**
     * Type of the Path, which is used to define the Loading-Strategy.
     * 
     * @return Type of the Path, which is used to define the Loading-Strategy.
     */
    PathType pathType() default PathType.CLASSPATH;

    /**
     * If true, this Configuration won't be eagerly loaded and a Provider will
     * be bound instead.
     * 
     * @return True if the Configuration should be bound eagerly.
     */
    boolean lazy() default false;

    /**
     * Class/Interface where the Configuration should be bound to.
     * 
     * @return Class/Interface where the Configuration should be bound to.
     */
    Class<? extends Object> bind() default Properties.class;

    public enum PathType {
	CLASSPATH, FILE, URL
    }
}
