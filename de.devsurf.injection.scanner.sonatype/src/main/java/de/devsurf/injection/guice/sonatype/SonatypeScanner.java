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
package de.devsurf.injection.guice.sonatype;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.guice.bean.scanners.ClassSpaceScanner;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.scanner.AnnotationListener;
import de.devsurf.injection.guice.scanner.ClasspathScanner;

/**
 * {@link ClasspathScanner} Implementation which uses the Google Guice-Extension
 * which is provided by Sonatype. This Implementation scans all provided
 * packages.
 * 
 * @author Daniel Manzke
 * 
 */
public class SonatypeScanner implements ClasspathScanner {
    private AnnotationCollector _collector;

    @Inject
    public SonatypeScanner(Set<AnnotationListener> listeners, @Named("packages") String... packages) {
	_collector = new AnnotationCollector();
	for (AnnotationListener listener : listeners) {
	    addAnnotationListener(listener);
	}
    }

    @Override
    public void addAnnotationListener(AnnotationListener listener) {
	_collector.addListener(listener);
    }

    @Override
    public void removeAnnotationListener(AnnotationListener listener) {
	_collector.removerListener(listener);
    }

    @Override
    public List<AnnotationListener> getAnnotationListeners() {
	return _collector.getListeners();
    }

    @Override
    public void excludePackage(String packageName) {
    }

    @Override
    public void includePackage(String packageName) {
    }

    @Override
    public void scan() throws IOException {
	ClassSpace space = new URLClassSpace(getClass().getClassLoader());
	ClassSpaceScanner scanner = new ClassSpaceScanner(space);
//	scanner.accept(new QualifiedTypeVisitor(new QualifiedTypeListener() {
//	    @SuppressWarnings("unchecked")
//	    @Override
//	    public void hear(Annotation qualifier, Class<?> qualifiedType, Object source) {
//		for (AnnotationListener listener : _listeners) {
//		    listener.found((Class<Object>) qualifiedType, Collections.singletonMap(
//			qualifier.annotationType().getName(), qualifier));
//		}
//	    }
//	}));
	scanner.accept(_collector);
    }
}
