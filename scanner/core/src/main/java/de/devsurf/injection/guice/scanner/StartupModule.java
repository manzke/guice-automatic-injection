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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;

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
public abstract class StartupModule extends AbstractModule {
    private String[] _packages;
    private Class<? extends ClasspathScanner> _scanner;
    private Logger _logger = Logger.getLogger(StartupModule.class.getName());

    public StartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	_packages = (packages == null ? new String[0] : packages);
	_scanner = scanner;
    }

    @Override
    protected void configure() {
	if (_logger.isLoggable(Level.FINE)) {
	    _logger.fine("Binding ClasspathScanner to " + _scanner.getName());
	    for (String p : _packages) {
		_logger.fine("Using Package " + p + " for scanning.");
	    }
	}

	bind(InstallationContext.class).asEagerSingleton();
	bind(ClasspathScanner.class).to(_scanner);
	bind(TypeLiteral.get(String[].class)).annotatedWith(Names.named("packages")).toInstance(
	    _packages);
	bind(DynamicModule.class).to(ScannerModule.class);
	bindAnnotationListeners();
    }

    protected abstract void bindAnnotationListeners();

    public static StartupModule create(Class<? extends ClasspathScanner> scanner,
	    String... packages) {
	return new DefaultStartupModule(scanner, packages);
    }

    public static class DefaultStartupModule extends StartupModule {

	public DefaultStartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	    super(scanner, packages);
	}

	@Override
	protected void bindAnnotationListeners() {
	    Multibinder<AnnotationListener> listeners = Multibinder.newSetBinder(binder(),
		AnnotationListener.class);
	    listeners.addBinding().to(AutoBind.AutoBindListener.class);
	    listeners.addBinding().to(GuiceModule.ModuleListener.class);
	}
    }
}
