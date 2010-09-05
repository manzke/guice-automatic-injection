/*******************************************************************************
 * Copyright 2010, Daniel Manzke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License.
 * 
 ******************************************************************************/
package de.devsurf.injection.guice.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import javax.inject.Qualifier;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Scopes;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.scanner.AnnotationListener;

/**
 * Annotate a Class which should be binded automatically. The Classpath Scanner,
 * will check for these classes. If the name()-Attribute is set (default is ""), 
 * the class will be bound to the implemented interfaces and a named annotation.
 * 
 * You can overwrite the interfaces, which should be used for binding the class. 
 * If bind()-Attribute is not set, the implemented interfaces will be used. If set
 * they will be ignored and overwritten.
 * 
 * If you annotate your class with {@link com.google.inject.Singleton} or {@link javax.inject.Singleton}
 * they will be also bound to the Singleton-Scope.
 * 
 * @author Daniel Manzke
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface AutoBind {
	String name() default "";
	Class<? extends Object>[] bind() default {};
	
	public class AutoBindListener implements AnnotationListener{
		private final Binder _binder;
		
		@Inject
		public AutoBindListener(Binder binder) {
			_binder = binder;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void found(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
			if(annotations.containsKey(AutoBind.class.getName())){
				AutoBind annotation = (AutoBind) annotations.get(AutoBind.class.getName());
				
				boolean overwriteInterfaces = (annotation.bind().length > 0);
				boolean nameIt = (annotation.name() != null && annotation.name().length() > 0);
				boolean asSingleton = (annotations.containsKey(com.google.inject.Singleton.class.getName()) || annotations.containsKey(javax.inject.Singleton.class.getName()));  
								
				Class<Object>[] interfaces = (overwriteInterfaces ? (Class<Object>[])annotation.bind() : (Class<Object>[]) annotatedClass.getInterfaces());
				for(Class<Object> interf : interfaces){
					ScopedBindingBuilder builder;
					if(nameIt){
						builder = _binder.bind(interf).annotatedWith(Names.named(annotation.name())).to(annotatedClass);
					}else{
						builder = _binder.bind(interf).to(annotatedClass);
					}		
					if(asSingleton){
						builder.in(Scopes.SINGLETON);
					}
				}	
			}
		}
	}
}
