package de.devsurf.injection.guice.scanner;

import java.io.IOException;


public interface ClasspathScanner {
	void scan() throws IOException;
	void addAnnotationListener(AnnotationListener listener);
	void removeAnnotationListener(AnnotationListener listener);
	void includePackage(String packageName);
	void excludePackage(String packageName);
}
