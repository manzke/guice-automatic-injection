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
package de.devsurf.injection.guice.scanner.asm.example.startupmodule;

import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.multibindings.Multibinder;

import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.ScannerFeature;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind.AutoBindListener;

/**
 * The {@link ExampleStartupModule} overwrites the
 * bindAnnotationListeners-Method, because our Example has several Classes
 * annotated with {@link AutoBind} and {@link GuiceModule}. Due the fact, that
 * our GuiceModule binds the {@link Example}-Interface to the
 * {@link ExampleImpl}-Class and the {@link AutoBindListener} too, we would get
 * a {@link CreationException}.
 * 
 * @author Daniel Manzke
 * 
 */
public class ExampleStartupModule extends StartupModule {

    public ExampleStartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	super(scanner, packages);
    }

    @Override
    protected Multibinder<ScannerFeature> bindFeatures(Binder binder) {
	Multibinder<ScannerFeature> listeners = Multibinder.newSetBinder(binder,
	    ScannerFeature.class);
	listeners.addBinding().to(GuiceModule.ModuleListener.class);
	return listeners;
    }

}
