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
package de.devsurf.injection.guice.scanner.annotations.features;

import static de.devsurf.injection.guice.scanner.annotations.To.Type.IMPLEMENTATION;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.scanner.BindingScannerFeature;
import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;
import de.devsurf.injection.guice.scanner.annotations.Bind;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;

@Singleton
public class AutoBindingFeature extends BindingScannerFeature {
	private Logger _logger = Logger.getLogger(AutoBindingFeature.class.getName());

	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		if (annotations.containsKey(Bind.class.getName())) {
			Bind annotation = (Bind) annotations.get(Bind.class.getName());
			if (!annotation.multiple() && !(annotation.to().value() == IMPLEMENTATION)) {
				return BindingStage.BINDING;
			}
		}
		return BindingStage.IGNORE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(final Class<Object> annotatedClass,
			final Map<String, Annotation> annotations) {
		Bind annotation = (Bind) annotations.get(Bind.class.getName());
		Map<String, Annotation> filtered = filter(annotations);

		final boolean asSingleton = (annotations.containsKey(com.google.inject.Singleton.class
			.getName()) || annotations.containsKey(javax.inject.Singleton.class.getName()));

		if (filtered.containsKey(Named.class.getName())) {
			Named named = (Named) filtered.remove(Named.class.getName());
			filtered.put(com.google.inject.name.Named.class.getName(), Names.named(named.value()));
		}else if(annotation.value().value().length() > 0){
			filtered.put(com.google.inject.name.Named.class.getName(), Names.named(annotation.value().value()));
		}

		Class<Object>[] interfaces;

		switch (annotation.to().value()) {
		case CUSTOM:
			interfaces = (Class<Object>[]) annotation.to().customs();
			break;
		case SUPER:
			Class<? super Object> superclass = annotatedClass.getSuperclass();
			if (Object.class.equals(superclass)) {
				interfaces = new Class[0];
			} else {
				interfaces = new Class[] { superclass };
			}

			break;
		case INTERFACES:
		default:
			interfaces = (Class<Object>[]) annotatedClass.getInterfaces();
			if(interfaces.length == 0){
				List<Class<?>> interfaceCollection = new ArrayList<Class<?>>();
				Class<? super Object> parent = annotatedClass.getSuperclass();
				while(parent != null && !parent.equals(Object.class)){
					Collections.addAll(interfaceCollection, parent.getInterfaces());
					parent = parent.getSuperclass();
				}
				interfaces = interfaceCollection.toArray(new Class[interfaceCollection.size()]);
			}
		}

		// TODO Should we add the Binding to the Super-Classes? Or only if
		// there are no Interfaces?
		for (Class<Object> interf : interfaces) {
			if (_logger.isLoggable(Level.FINE)) {
				_logger.fine(String.format("Binding Class %s to Interface %s. Singleton? %s ",
					annotatedClass, interf, asSingleton));
			}

			if (filtered.size() > 0) {
				for (Annotation anno : filtered.values()) {
					bind(annotatedClass, interf, anno, (asSingleton ? Scopes.SINGLETON : null));
				}
			} else {
				bind(annotatedClass, interf, null, (asSingleton ? Scopes.SINGLETON : null));
			}
		}
	}

	protected Map<String, Annotation> filter(final Map<String, Annotation> annotations) {
		Map<String, Annotation> filtered = new HashMap<String, Annotation>(annotations);

		filtered.remove(Bind.class.getName());
		filtered.remove(GuiceModule.class.getName());
		filtered.remove(com.google.inject.Singleton.class.getName());
		filtered.remove(javax.inject.Singleton.class.getName());
		filtered.remove("de.devsurf.injection.guice.configuration.Configuration");

		return filtered;
	}
}
