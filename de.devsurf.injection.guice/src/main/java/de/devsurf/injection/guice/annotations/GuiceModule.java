package de.devsurf.injection.guice.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import javax.inject.Qualifier;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Module;

import de.devsurf.injection.guice.scanner.AnnotationListener;

/**
 * Annotate a Module with the GuiceModule-Annotation and
 * it will be installed automatically.
 * 
 * @author Daniel Manzke
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface GuiceModule {
	public class GuiceModuleListener implements AnnotationListener{
		private final Binder _binder;
		
		@Inject
		public GuiceModuleListener(Binder binder) {
			_binder = binder;
		}
		
		@Override
		public void found(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
			if(annotations.containsKey(GuiceModule.class.getName())){
				try {
					_binder.install((Module) annotatedClass.newInstance());
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}			
			}
		}
	}
}
