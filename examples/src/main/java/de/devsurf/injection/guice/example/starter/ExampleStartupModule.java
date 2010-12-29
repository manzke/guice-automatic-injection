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
package de.devsurf.injection.guice.example.starter;

import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.multibindings.Multibinder;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.annotations.GuiceModule;
import de.devsurf.injection.guice.annotations.features.AutoBindingFeature;
import de.devsurf.injection.guice.annotations.features.MultiBindingFeature;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.features.ScannerFeature;

/**
 * The {@link ExampleStartupModule} overwrites the
 * bindAnnotationListeners-Method, because our Example has several Classes
 * annotated with {@link Bind} and {@link GuiceModule}. Due the fact, that our
 * GuiceModule binds the {@link Example}-Interface to the {@link ExampleImpl}
 * -Class and the {@link AutoBindingFeature} too, we would get a
 * {@link CreationException}.
 * 
 * @author Daniel Manzke
 * 
 */
public class ExampleStartupModule extends StartupModule {

	public ExampleStartupModule(Class<? extends ClasspathScanner> scanner, PackageFilter... packages) {
		super(scanner, packages);
	}

	@Override
	protected Multibinder<ScannerFeature> bindFeatures(Binder binder) {
		Multibinder<ScannerFeature> listeners = Multibinder.newSetBinder(binder,
			ScannerFeature.class);
		listeners.addBinding().to(MultiBindingFeature.class);
		return listeners;
	}

}
