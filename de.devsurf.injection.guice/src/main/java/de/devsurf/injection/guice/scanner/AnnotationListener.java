package de.devsurf.injection.guice.scanner;

import java.lang.annotation.Annotation;
import java.util.Map;


public interface AnnotationListener {
	void found(Class<Object> annotatedClass, Map<String, Annotation> annotations);
}
