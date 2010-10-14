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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import com.googlecode.rocoto.simpleconfig.SimpleConfigurationModule;

import de.devsurf.injection.guice.configuration.rocoto.ConfigurationModule;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;
import de.devsurf.injection.guice.scanner.annotations.features.AutoBindingFeature;
import de.devsurf.injection.guice.scanner.annotations.features.ImplementationBindingFeature;
import de.devsurf.injection.guice.scanner.annotations.features.MultiBindingFeature;

/**
 * The StartupModule is used for creating an initial Injector, which binds and
 * instantiates the Scanning module. Due the fact that we have multiple Scanner
 * Implementations, you have to pass the Class for the Scanner and the Packages
 * which should be scanned. You can override the bindAnnotationListeners-Method,
 * to add your own {@link ScannerFeature}.
 * 
 * @author Daniel Manzke
 * 
 */
public abstract class StartupModule extends SimpleConfigurationModule {
	protected Logger _logger = Logger.getLogger(StartupModule.class.getName());
	protected String[] _packages;
	protected Class<? extends ClasspathScanner> _scanner;
	protected List<Class<? extends ScannerFeature>> _features = new ArrayList<Class<? extends ScannerFeature>>();
	protected boolean bindSystemProperties;
	protected boolean bindEnvironment;

	public StartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
		_packages = (packages == null ? new String[0] : packages);
		_scanner = scanner;
	}

	@Override
	public void configure() {
		List<String> packages = new ArrayList<String>();
		Collections.addAll(packages, _packages);
		Collections.addAll(packages, bindPackages());

		_packages = packages.toArray(new String[packages.size()]);
		Module scannerModule = new AbstractModule() {
			@Override
			protected void configure() {
				Binder binder = binder();
				if (_logger.isLoggable(Level.FINE)) {
					_logger.fine("Binding ClasspathScanner to " + _scanner.getName());
					for (String p : _packages) {
						_logger.fine("Using Package " + p + " for scanning.");
					}
				}

				binder.bind(InstallationContext.class).asEagerSingleton();
				binder.bind(ClasspathScanner.class).to(_scanner);
				binder.bind(TypeLiteral.get(String[].class)).annotatedWith(Names.named("packages"))
					.toInstance(_packages);
				Set<URL> classpath = findClassPaths();
				binder.bind(TypeLiteral.get(URL[].class)).annotatedWith(Names.named("classpath"))
					.toInstance(classpath.toArray(new URL[classpath.size()]));

				bindFeatures(binder);
			}
		};

		SimpleConfigurationModule configurationModule = new SimpleConfigurationModule();
		if (bindSystemProperties) {
			configurationModule.addSystemProperties();
		}
		if (bindEnvironment) {
			configurationModule.addEnvironmentVariables();
		}

		Injector internal = Guice.createInjector(scannerModule, configurationModule);
		binder().install(internal.getInstance(ScannerModule.class));
	}

	protected abstract Multibinder<ScannerFeature> bindFeatures(Binder binder);

	protected String[] bindPackages() {
		return new String[0];
	}

	protected Set<URL> findClassPaths() {
		Set<URL> urlSet = new HashSet<URL>();

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		while (loader != null) {
			if (loader instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) loader).getURLs();
				Collections.addAll(urlSet, urls);
			}
			loader = loader.getParent();
		}

		String classpath = System.getProperty("java.class.path");
		try {
			classpath = classpath + File.pathSeparator
					+ new File(StartupModule.class.getResource("/").toURI()).getAbsolutePath();
		} catch (URISyntaxException e) {
			// ignore
		}

		for (String path : classpath.split(File.pathSeparator)) {
			File file = new File(path);
			try {
				if (file.exists()) {
					urlSet.add(file.toURI().toURL());
				}
			} catch (MalformedURLException e) {
				_logger.log(Level.INFO, "Found invalid URL in Classpath: " + path, e);
			}
		}

		return urlSet;
	}

	public void addFeature(Class<? extends ScannerFeature> listener) {
		_features.add(listener);
	}

	public void bindSystemProperties() {
		bindSystemProperties = true;
	}

	public void bindEnvironment() {
		bindEnvironment = true;
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
		protected Multibinder<ScannerFeature> bindFeatures(Binder binder) {
			Multibinder<ScannerFeature> listeners = Multibinder.newSetBinder(binder,
				ScannerFeature.class);
			listeners.addBinding().to(AutoBindingFeature.class);
			listeners.addBinding().to(ImplementationBindingFeature.class);
			listeners.addBinding().to(MultiBindingFeature.class);
			listeners.addBinding().to(GuiceModule.ModuleListener.class);

			for (Class<? extends ScannerFeature> listener : _features) {
				listeners.addBinding().to(listener);
			}

			return listeners;
		}

		@Override
		protected String[] bindPackages() {
			return new String[] { ConfigurationModule.class.getPackage().getName() };
		}

	}
}
