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
package de.devsurf.injection.guice.scanner;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Binder;
import com.google.inject.Inject;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;

/**
 * The ScannerModule will be injected with a ClasspathScanner and the needed
 * Annotation Listeners will be added. The attached Listeners will install all
 * Modules annotated with {@link GuiceModule} and bind all Beans annotated with
 * {@link AutoBind}.
 * 
 * @author Daniel Manzke
 * 
 */
public class ScannerModule implements DynamicModule {
    private ClasspathScanner _scanner;
    private Logger _logger = Logger.getLogger(ScannerModule.class.getName());

    @Inject
    public ScannerModule(ClasspathScanner scanner) {
	_scanner = scanner;
    }

    @Override
    public void configure(Binder binder) {
	List<AnnotationListener> listeners = _scanner.getAnnotationListeners();
	for (AnnotationListener listener : listeners) {
	    if (listener instanceof GuiceAnnotationListener) {
		((GuiceAnnotationListener) listener).setBinder(binder);
		if (_logger.isLoggable(Level.FINE)) {
		    _logger.fine("Binding AnnotationListeners " + listener.getClass().getName());
		}
	    }
	}
	if (_logger.isLoggable(Level.FINE)) {
	    _logger.fine("Binding ClasspathScanner to " + _scanner.getClass().getName());
	}
	try {
	    _scanner.scan();
	} catch (IOException e) {
	    _logger.log(Level.SEVERE, "Failure while Scanning the Classpath for Classes with Annotations.", e);
	}
    }
}
