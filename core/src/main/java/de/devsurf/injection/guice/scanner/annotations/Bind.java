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
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.scanner.BindingScannerFeature;
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
public @interface Bind {
    com.google.inject.name.Named name() default @com.google.inject.name.Named("");

    boolean multiple() default false;

    /**
     * Overwrite the Classes/Interfaces the annotated Class should be bound to.
     * 
     * @return All Classes/Interfaces the annotated Class should be bound to. If
     *         empty, the implemented Interfaces will be used.
     */
    Class<? extends Object>[] to() default { Interfaces.class };
    
    public static class Interfaces{}

    @Singleton
    public class AutoBindingFeature extends BindingScannerFeature {
	private Logger _logger = Logger.getLogger(AutoBindingFeature.class.getName());

	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
	    if (annotations.containsKey(Bind.class.getName())) {
		Bind annotation = (Bind) annotations.get(Bind.class.getName());
		if (!annotation.multiple()) {
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

	    final boolean useInterfaces = ((annotation.to().length > 0) && (annotation.to()[0].equals(Interfaces.class)));
	    final boolean asSingleton = (annotations.containsKey(com.google.inject.Singleton.class
		.getName()) || annotations.containsKey(javax.inject.Singleton.class.getName()));

	    if (filtered.containsKey(Named.class.getName())) {
		Named named = (Named) filtered.remove(Named.class.getName());
		filtered.put(com.google.inject.name.Named.class.getName(), Names.named(named
		    .value()));
	    }

	    final Class<Object>[] interfaces = (useInterfaces ? (Class<Object>[]) annotatedClass.getInterfaces() : (Class<Object>[]) annotation.to());
	    // TODO Should we add the Binding to the Super-Classes? Or only if
	    // there are no Interfaces?
	    for (Class<Object> interf : interfaces) {
		if (_logger.isLoggable(Level.FINE)) {
		    _logger
			.fine(String
			    .format(
				"Binding Class %s to Interface %s. Use original Interfaces? %s Singleton? %s ",
				annotatedClass, interf, useInterfaces, asSingleton));
		}

		if (filtered.size() > 0) {
		    for (Annotation anno : filtered.values()) {
			bind(annotatedClass, interf, anno, (asSingleton ? Scopes.SINGLETON : null));
		    }
		} else {
		    bind(annotatedClass, interf, null, (asSingleton ? Scopes.SINGLETON : null));
		}
	    }

	    if (interfaces.length == 0) {
		bind(annotatedClass, null, (asSingleton ? Scopes.SINGLETON : null));
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
		Annotation annotation, Scope scope) {
	    Multibinder<T> builder;
	    synchronized (_binder) {
		if (annotation != null) {
		    builder = Multibinder.newSetBinder(_binder, interf, annotation);
		} else {
		    builder = Multibinder.newSetBinder(_binder, interf);
		}

		ScopedBindingBuilder scopedBindingBuilder = builder.addBinding().to(implementationClass);
		if (scope != null) {
		    scopedBindingBuilder.in(scope);
		}
	    }
	}
    }
}
