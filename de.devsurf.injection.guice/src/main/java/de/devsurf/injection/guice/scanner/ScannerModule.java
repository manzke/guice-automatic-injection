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

import com.google.inject.Binder;
import com.google.inject.Inject;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.annotations.AutoBind;
import de.devsurf.injection.guice.annotations.GuiceModule;

/**
 * The ScannerModule will be injected with a ClasspathScanner and
 * the needed Annotation Listeners will be added. The attached Listeners
 * will install all Modules annotated with {@link GuiceModule} and bind 
 * all Beans annotated with {@link AutoBind}.
 * 
 * @author Daniel Manzke
 *
 */
public class ScannerModule implements DynamicModule {
	private ClasspathScanner _scanner;
	
	@Inject
	public ScannerModule(ClasspathScanner scanner) {
		_scanner = scanner;
	}

	@Override
	public void configure(Binder binder) {	
		_scanner.addAnnotationListener(new GuiceModule.GuiceModuleListener(binder));
		_scanner.addAnnotationListener(new AutoBind.AutoBindListener(binder));

		try {
			_scanner.scan();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
