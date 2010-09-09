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
package de.devsurf.injection.guice.scanner.sonatype.example.autobind;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;
import de.devsurf.injection.guice.scanner.sonatype.SonatypeScanner;

/**
 * Example Application, which creates a new Injector with the help of the
 * provided {@link StartupModule}. It passes the {@link SonatypeScanner} class
 * for the {@link ClasspathScanner} and the packages (de.devsurf) which should
 * be scanned. The {@link StartupModule} binds these parameter, so we are able
 * to create and inject our {@link DynamicModule}. This Module uses the
 * {@link ClasspathScanner} to explore the Classpath and scans for Annotations.
 * 
 * All recognized Classes annotated with {@link GuiceModule} are installed in
 * the child injector and with {@link AutoBind} are automatically bound.
 * 
 * @author Daniel Manzke
 * 
 */
public class ExampleApp {
    public static void main(String[] args) throws IOException {
	Injector injector = Guice.createInjector(new ExampleStartupModule(SonatypeScanner.class,
	    ExampleApp.class.getPackage().getName()));
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	injector = injector.createChildInjector(dynamicModule);

	System.out.println(injector.getInstance(Example.class).sayHello());
    }
}
