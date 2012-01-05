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
package de.devsurf.injection.guice.annotations.features;

import static de.devsurf.injection.guice.annotations.To.Type.IMPLEMENTATION;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.logging.Level;

import javax.inject.Singleton;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.install.BindingStage;

@Singleton
public class ImplementationBindingFeature extends AutoBindingFeature {
	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		if (annotations.containsKey(Bind.class.getName())) {
			Bind annotation = (Bind) annotations.get(Bind.class.getName());
			if (!annotation.multiple() && (annotation.to().value() == IMPLEMENTATION)) {
				return BindingStage.BINDING;
			}
		}
		return BindingStage.IGNORE;
	}

	@Override
	public void process(final Class<Object> annotatedClass,
			final Map<String, Annotation> annotations) {
		final boolean asSingleton = (annotations.containsKey(com.google.inject.Singleton.class
			.getName()) || annotations.containsKey(javax.inject.Singleton.class.getName()));
		if (_logger.isLoggable(Level.FINE)) {
			_logger.fine(String.format("Binding Class %s. Singleton? %s ",
				annotatedClass, asSingleton));
		}
		bind(annotatedClass, null, (asSingleton ? Singleton.class : null));
	}
}