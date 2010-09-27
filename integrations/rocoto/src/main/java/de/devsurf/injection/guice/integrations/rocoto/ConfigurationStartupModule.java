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
package de.devsurf.injection.guice.integrations.rocoto;

import com.google.inject.multibindings.Multibinder;

import de.devsurf.injection.guice.scanner.ScannerFeature;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.StartupModule.DefaultStartupModule;

/**
 * Startup-Module which could be used, so you don't have to specify
 * {@link RocotoConfigurationFeature}, if this Feature should be used, too.
 * 
 * @author Daniel Manzke
 * 
 */
public class ConfigurationStartupModule extends DefaultStartupModule {
    public ConfigurationStartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	super(scanner, packages);
    }

    @Override
    protected Multibinder<ScannerFeature> bindFeatures() {
	super.bindFeatures();
	Multibinder<ScannerFeature> listeners = Multibinder.newSetBinder(binder(),
	    ScannerFeature.class);
	listeners.addBinding().to(RocotoConfigurationFeature.class);

	return listeners;
    }
}
