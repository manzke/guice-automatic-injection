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
package de.devsurf.injection.guice.scanner.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Qualifier;

import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.scanner.GuiceAnnotationListener;
import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;

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
@Target( { ElementType.TYPE })
public @interface AutoBind {
    Class<? extends Object>[] bind() default {};

    @Singleton
    public class AutoBindListener extends GuiceAnnotationListener {
	private Logger _logger = Logger.getLogger(AutoBindListener.class.getName());

	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
	    if (annotations.containsKey(AutoBind.class.getName())
		    && !annotations.containsKey(MultiBinding.class.getName())) {
		return BindingStage.BINDING;
	    }
	    return BindingStage.IGNORE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(final Class<Object> annotatedClass,
		final Map<String, Annotation> annotations) {
	    AutoBind annotation = (AutoBind) annotations.get(AutoBind.class.getName());
	    Map<String, Annotation> filtered = filter(annotations);

	    final boolean overwriteInterfaces = (annotation.bind().length > 0);
	    if (annotations.containsKey(Named.class.getName())) {
		String name = ((Named) annotations.get(Named.class.getName())).value();
		filtered.put(com.google.inject.name.Named.class.getName(), Names.named(name));
	    }

	    final boolean asSingleton = (annotations.containsKey(com.google.inject.Singleton.class
		.getName()) || annotations.containsKey(javax.inject.Singleton.class.getName()));

	    final Class<Object>[] interfaces = (overwriteInterfaces ? (Class<Object>[]) annotation
		.bind() : (Class<Object>[]) annotatedClass.getInterfaces());

	    for (Class<Object> interf : interfaces) {
		if (_logger.isLoggable(Level.FINE)) {
		    _logger
			.fine(String
			    .format(
				"Binding Class %s to Interface %s. Overwriting original Interfaces? %s Singleton? %s ",
				annotatedClass, interf, overwriteInterfaces, asSingleton));
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

	    filtered.remove(AutoBind.class.getName());
	    filtered.remove(GuiceModule.class.getName());
	    filtered.remove(Named.class.getName());
	    filtered.remove(com.google.inject.Singleton.class.getName());
	    filtered.remove(javax.inject.Singleton.class.getName());

	    return filtered;
	}

	@SuppressWarnings("unchecked")
	protected void bind(Class<Object> impl, Class<Object> interf, Annotation annotation,
		Scope scope) {
	    LinkedBindingBuilder builder;
	    synchronized (_binder) {
		builder = _binder.bind(interf);
		if (annotation != null) {
		    builder = ((AnnotatedBindingBuilder) builder).annotatedWith(annotation);
		}
		builder.to(impl);
		if (scope != null) {
		    builder.in(scope);
		}
	    }
	}
    }
}
