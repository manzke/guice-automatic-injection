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

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.logging.Level;

import javax.inject.Singleton;

import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.install.BindingStage;
import de.devsurf.injection.guice.install.bindjob.BindingJob;
import de.devsurf.injection.guice.install.bindjob.MultiBindingJob;

@Singleton
public class MultiBindingFeature extends AutoBindingFeature {
	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		if (annotations.containsKey(Bind.class.getName())) {
			Bind annotation = (Bind) annotations.get(Bind.class.getName());
			if (annotation.multiple()) {
				return BindingStage.BINDING;
			}
		}
		return BindingStage.IGNORE;
	}
	
	@Override
	protected <T, V extends T> void bind(Class<V> implementationClass, Class<T> interf,
			Annotation annotation, Class<? extends Annotation> scope) {
		BindingJob job = new MultiBindingJob(scope, annotation, implementationClass.getName(),
			interf.getName());
		
		if (!tracer.contains(job)) {
			Multibinder<T> builder;
			synchronized (_binder) {
				if (annotation != null) {
					builder = Multibinder.newSetBinder(_binder, interf, annotation);
				} else {
					builder = Multibinder.newSetBinder(_binder, interf);
				}

				ScopedBindingBuilder scopedBindingBuilder = builder.addBinding().to(
					implementationClass);
				if (scope != null) {
					scopedBindingBuilder.in(scope);
				}
			}
			tracer.add(job);
		} else {
			if (_logger.isLoggable(Level.FINE)) {
				_logger.log(Level.FINE, "Ignoring Multi-BindingJob \"" + job.toString()
						+ "\", because it was already bound.", new Exception("Ignoring Multi-BindingJob \"" + job.toString()
							+ "\", because it was already bound."));
			} else if (_logger.isLoggable(Level.INFO)) {
				_logger.log(Level.INFO, "Ignoring Multi-BindingJob \"" + job.toString()
						+ "\", because it was already bound.");
			}
		}
	}
}