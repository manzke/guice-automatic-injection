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
package de.devsurf.injection.guice.scanner.reflections.example.autobind;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.example.starter.ExampleApplication;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;
import de.devsurf.injection.guice.scanner.annotations.MultiBinding;
import de.devsurf.injection.guice.scanner.reflections.ReflectionsScanner;

/**
 * Example Application, which creates a new Injector with the help of the
 * provided {@link StartupModule}. It passes the {@link ReflectionsScanner}
 * class for the {@link ClasspathScanner} and the packages (de.devsurf) which
 * should be scanned. The {@link StartupModule} binds these parameter, so we are
 * able to create and inject our {@link DynamicModule}. This Module uses the
 * {@link ClasspathScanner} to explore the Classpath and scans for Annotations.
 * 
 * All recognized Classes annotated with {@link GuiceModule} are installed in
 * the child injector and with {@link AutoBind} are automatically bound.
 * 
 * @author Daniel Manzke
 * 
 */
@AutoBind
@MultiBinding
public class ExampleApp implements ExampleApplication{
    @Override
    public void run(){
	System.out.println(ExampleApp.class.getPackage().getName());
	Injector injector = Guice.createInjector(StartupModule.create(ReflectionsScanner.class,
	    ExampleApp.class.getPackage().getName()));

	System.out.println(injector.getInstance(Example.class).sayHello());
    }
    
    public static void main(String[] args) {
	new ExampleApp().run();
    }
}
