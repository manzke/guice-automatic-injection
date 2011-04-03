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

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.annotations.GuiceModule;
import de.devsurf.injection.guice.example.starter.ExampleApplication;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.ScannerModule;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;

/**
 * Example Application, which creates a new Injector with the help of our own
 * {@link ExampleStartupModule}. It passes the {@link ASMClasspathScanner} class
 * for the {@link ClasspathScanner} and the packages (de.devsurf) which should
 * be scanned. The {@link StartupModule} binds these parameter, so we are able
 * to create and inject our {@link ScannerModule}. This Module uses the
 * {@link ClasspathScanner} to explore the Classpath and scans for Annotations.
 * 
 * All recognized Classes annotated with {@link GuiceModule} are installed in
 * the child injector and with {@link Bind} are automatically bound.
 * 
 * @author Daniel Manzke
 * 
 */
@Bind(multiple = true)
public class ExampleApp implements ExampleApplication {
	@Override
	public void run() {
		Injector injector = Guice.createInjector(new ExampleStartupModule(
			ASMClasspathScanner.class, PackageFilter.create(ExampleApp.class)));
		System.out.println(injector.getInstance(Example.class).sayHello());
	}

	public static void main(String[] args) {
		new ExampleApp().run();
	}
}
