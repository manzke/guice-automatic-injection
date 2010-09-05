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
package de.devsurf.injection.guice.asm.example.automodule;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.StartupModule;
import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.asm.VirtualClasspathReader;

public class ExampleApp 
{
    public static void main( String[] args ) throws IOException
    {
    	Injector injector = Guice.createInjector(new StartupModule(VirtualClasspathReader.class, "de.devsurf"));
    	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
    	injector = injector.createChildInjector(dynamicModule);
    	System.out.println(injector.getInstance(Example.class).sayHello());
    }
}
