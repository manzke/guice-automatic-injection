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
package de.devsurf.injection.guice.scanner.sonatype;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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
    private Logger _logger = Logger.getLogger(SonatypeScanner.class.getName());
    private FilterAnnotationCollector _collector;
    private LinkedList<Pattern> _packagePatterns;

    @Inject
    public SonatypeScanner(Set<AnnotationListener> listeners, @Named("packages") String... packages) {
	_packagePatterns = new LinkedList<Pattern>();
	_collector = new FilterAnnotationCollector() {
	    @Override
	    public boolean matches(String name) {
		for (Pattern pattern : _packagePatterns) {
		    if (pattern.matcher(name).matches()) {
			return true;
		    }
		}
		return false;
	    }
	};
	
	for (String p : packages) {
	    includePackage(p);
	}
	
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
	String pattern = ".*" + packageName + ".*";
	if(_logger.isLoggable(Level.FINE)){
	    _logger.fine("Including Package for scanning: "+packageName+" generating Pattern: "+pattern);
	}
	_packagePatterns.add(Pattern.compile(pattern));
    }

    @Override
    public void scan() throws IOException {
	ClassSpace space = new URLClassSpace(getClass().getClassLoader());
	ClassSpaceScanner scanner = new ClassSpaceScanner(space);
	scanner.accept(_collector);
    }
}
