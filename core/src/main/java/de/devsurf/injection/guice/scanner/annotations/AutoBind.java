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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;
import javax.inject.Qualifier;

import com.google.inject.Scopes;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.scanner.GuiceAnnotationListener;

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
@Target({ElementType.TYPE})
public @interface AutoBind {
    Class<? extends Object>[] bind() default {};

    public class AutoBindListener extends GuiceAnnotationListener {
	private Logger _logger = Logger.getLogger(AutoBindListener.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public void found(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
	    if (annotations.containsKey(AutoBind.class.getName())) {
		AutoBind annotation = (AutoBind) annotations.get(AutoBind.class.getName());

		boolean overwriteInterfaces = (annotation.bind().length > 0);
		boolean nameIt = annotations.containsKey(Named.class.getName());
		String name = null;
		if(nameIt){
		    name = ((Named)annotations.get(Named.class.getName())).value();
		    if(name.length() == 0){
			name = annotatedClass.getName();
		    }
		}
		boolean multiple = annotations.containsKey(MultiBinding.class.getName());
		boolean asSingleton = (annotations.containsKey(com.google.inject.Singleton.class
		    .getName()) || annotations.containsKey(javax.inject.Singleton.class.getName()));
		
		

		Class<Object>[] interfaces = (overwriteInterfaces ? (Class<Object>[]) annotation
		    .bind() : (Class<Object>[]) annotatedClass.getInterfaces());
		for (Class<Object> interf : interfaces) {
		    if(_logger.isLoggable(Level.FINE)){
			_logger.fine(String.format("Binding Class %s to Interface %s. Named? %s Overwriting original Interfaces? %s Singleton? %s Multiple? %s", annotatedClass, interf, nameIt, overwriteInterfaces, asSingleton));
		    }
		    LinkedBindingBuilder builder;
		    synchronized (_binder) {
			if(multiple){
			    builder = Multibinder.newSetBinder(_binder, interf).addBinding();
			    nameIt = false;
			}else{
			    builder = _binder.bind(interf);
			}
			if (nameIt) {
			    ((AnnotatedBindingBuilder)builder).annotatedWith(Names.named(name)).to(annotatedClass);
			} else {
			    builder.to(annotatedClass);
			}
			if (asSingleton) {
			    builder.in(Scopes.SINGLETON);
			}
		    }
		}
	    }
	}
    }
}
