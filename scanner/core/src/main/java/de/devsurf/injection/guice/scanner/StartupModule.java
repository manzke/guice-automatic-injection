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
package de.devsurf.injection.guice.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;
import de.devsurf.injection.guice.scanner.annotations.MultiBinding;

/**
 * The StartupModule is used for creating an initial Injector, which binds and
 * instantiates the Scanning module. Due the fact that we have multiple Scanner
 * Implementations, you have to pass the Class for the Scanner and the Packages
 * which should be scanned. You can override the bindAnnotationListeners-Method,
 * to add your own {@link AnnotationListener}.
 * 
 * @author Daniel Manzke
 * 
 */
public abstract class StartupModule implements Module {
    protected String[] _packages;
    protected Class<? extends ClasspathScanner> _scanner;
    protected List<Class<? extends AnnotationListener>> _features = new ArrayList<Class<? extends AnnotationListener>>();
    protected Logger _logger = Logger.getLogger(StartupModule.class.getName());
    protected Binder _binder;

    public StartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	_packages = (packages == null ? new String[0] : packages);
	_scanner = scanner;
    }
    
    protected Binder binder(){
	return _binder;
    }

    @Override
    public void configure(Binder binder) {
	_binder = binder;
	if (_logger.isLoggable(Level.FINE)) {
	    _logger.fine("Binding ClasspathScanner to " + _scanner.getName());
	    for (String p : _packages) {
		_logger.fine("Using Package " + p + " for scanning.");
	    }
	}

	binder.bind(InstallationContext.class).asEagerSingleton();
	binder.bind(ClasspathScanner.class).to(_scanner);
	binder.bind(TypeLiteral.get(String[].class)).annotatedWith(Names.named("packages")).toInstance(
	    _packages);
	binder.bind(DynamicModule.class).to(ScannerModule.class);
	bindFeatures();
    }

    protected abstract Multibinder<AnnotationListener> bindFeatures();
    
    public void addFeature(Class<? extends AnnotationListener> listener){
	_features.add(listener);
    }

    public static StartupModule create(Class<? extends ClasspathScanner> scanner,
	    String... packages) {
	return new DefaultStartupModule(scanner, packages);
    }

    public static class DefaultStartupModule extends StartupModule {

	public DefaultStartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	    super(scanner, packages);
	}

	@Override
	protected Multibinder<AnnotationListener> bindFeatures() {
	    Multibinder<AnnotationListener> listeners = Multibinder.newSetBinder(_binder,
		AnnotationListener.class);
	    listeners.addBinding().to(AutoBind.AutoBindListener.class);
	    listeners.addBinding().to(MultiBinding.MultiBindListener.class);
	    listeners.addBinding().to(GuiceModule.ModuleListener.class);
	    
	    for(Class<? extends AnnotationListener> listener : _features){
		listeners.addBinding().to(listener);
	    }
	    
	    return listeners;
	}
    }
}
