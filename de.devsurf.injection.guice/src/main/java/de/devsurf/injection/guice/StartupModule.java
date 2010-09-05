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
package de.devsurf.injection.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.ScannerModule;

/**
 * The StartupModule is used for creating an initial Injector, which is used 
 * for binding and instantiating the Scanning module. Due the fact that we have
 * multiple Scanner Implementations, you have to pass the Class for the Scanner 
 * and the Packages which should be scanned. 
 * 
 * @author Daniel Manzke
 *
 */
public class StartupModule extends AbstractModule{
	private String[] _packages;
	private Class<? extends ClasspathScanner> _scanner;
	
	public StartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
		_packages = (packages == null ? new String[0] : packages);
		_scanner = scanner;
	}

	@Override
	protected void configure() {
		bind(ClasspathScanner.class).to(_scanner);
		bind(TypeLiteral.get(String[].class)).annotatedWith(Names.named("packages")).toInstance(_packages);
		bind(DynamicModule.class).to(ScannerModule.class);
	}
}
