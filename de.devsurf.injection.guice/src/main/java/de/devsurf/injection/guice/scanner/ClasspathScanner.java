package de.devsurf.injection.guice.scanner;

import java.io.IOException;


/**
 * Interface which is used to bind a ClasspathScanner to. If you
 * want to use your own, just implement this one and pass the Class
 * to the StartupModule constructor.
 * 
 * @author Daniel Manzke
 *
 */
public interface ClasspathScanner {
	void scan() throws IOException;
	void addAnnotationListener(AnnotationListener listener);
	void removeAnnotationListener(AnnotationListener listener);
	void includePackage(String packageName);
	void excludePackage(String packageName);
}
