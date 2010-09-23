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
import java.util.Map;

import javax.inject.Qualifier;

import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;

import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;
import de.devsurf.injection.guice.scanner.annotations.AutoBind.AutoBindListener;

/**
 * Annotate a Class which should be binded multiple Times. Using the Google
 * Guice Multibinding-Extension for it.
 * 
 * @author Daniel Manzke
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target({ElementType.TYPE})
public @interface MultiBinding {
    @Singleton
    public class MultiBindListener extends AutoBindListener {
	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
	    if (annotations.containsKey(AutoBind.class.getName())
		    && annotations.containsKey(MultiBinding.class.getName())) {
		return BindingStage.BINDING;
	    }
	    return BindingStage.IGNORE;
	}
	
	protected Map<String, Annotation> filter(final Map<String, Annotation> annotations) {
	    Map<String, Annotation> filtered = super.filter(annotations);
	    
	    filtered.remove(MultiBinding.class.getName());

	    return filtered;
	}

	@SuppressWarnings("unchecked")
	protected void bind(Class<Object> impl, Class<Object> interf, Annotation annotation, Scope scope) {
	    LinkedBindingBuilder builder;
	    synchronized (_binder) {
		if (annotation != null) {
		    builder = Multibinder.newSetBinder(_binder, interf, annotation).addBinding();
		} else {
		    builder = Multibinder.newSetBinder(_binder, interf).addBinding();
		}
		builder.to(impl);
		if (scope != null) {
		    builder.in(scope);
		}
	    }
	}
    }
}
