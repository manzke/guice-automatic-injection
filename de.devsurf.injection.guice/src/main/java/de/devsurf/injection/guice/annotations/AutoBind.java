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
