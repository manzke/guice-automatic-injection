package de.devsurf.injection.guice.sonatype;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedList;

import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.guice.bean.scanners.ClassSpaceScanner;
import org.sonatype.guice.bean.scanners.QualifiedTypeListener;
import org.sonatype.guice.bean.scanners.QualifiedTypeVisitor;

import de.devsurf.injection.guice.scanner.AnnotationListener;
import de.devsurf.injection.guice.scanner.ClasspathScanner;

public class SonatypeScanner implements ClasspathScanner {
	private LinkedList<AnnotationListener> _listeners;
	
	public SonatypeScanner() {
		_listeners = new LinkedList<AnnotationListener>();
	}
	
	@Override
	public void addAnnotationListener(AnnotationListener listener) {
		_listeners.add(listener);
	}

	@Override
	public void excludePackage(String packageName) {
	}

	@Override
	public void includePackage(String packageName) {
	}

	@Override
	public void removeAnnotationListener(AnnotationListener listener) {
	}

	@Override
	public void scan() throws IOException {
		ClassSpace space = new URLClassSpace(getClass().getClassLoader());
		ClassSpaceScanner scanner = new ClassSpaceScanner(space);
		scanner.accept(new QualifiedTypeVisitor(new QualifiedTypeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void hear(Annotation qualifier, Class<?> qualifiedType, Object source) {
				for(AnnotationListener listener : _listeners){
					listener.found((Class<Object>) qualifiedType, Collections.singletonMap(qualifier.annotationType().getName(), qualifier));
				}
			}
		}));
	}
}
