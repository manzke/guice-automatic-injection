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

import java.util.Set;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.asm.VirtualClasspathReader;

public class ExampleStarter {
    public static void main(String[] args) {
	Injector injector = Guice.createInjector(new ExampleStartupModule(VirtualClasspathReader.class,
	    "de.devsurf.injection.guice"));
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	injector = injector.createChildInjector(dynamicModule);

	Set<ExampleApplication> apps = injector.getInstance(ExampleContainer.class).applications;
	for(ExampleApplication app : apps){
	    System.out.println("Starting App: "+app.getClass().getName());
	    app.run();
	    System.out.println();
	}
    }
    
    public static class ExampleContainer{
	@Inject
	public Set<ExampleApplication> applications;
    }
}
