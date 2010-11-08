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
package de.devsurf.injection.guice.integrations.metro;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.xml.ws.spi.WebServiceFeatureAnnotation;

import com.sun.xml.ws.api.server.InstanceResolverAnnotation;

@Retention(RUNTIME)
@Target(TYPE)
@Documented
@WebServiceFeatureAnnotation(id = GuiceManagedFeature.ID, bean = GuiceManagedFeature.class)
@InstanceResolverAnnotation(AutomaticGuiceManager.class)
public @interface GuiceManaged {
}
