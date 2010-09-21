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

package de.devsurf.injection.guice.scanner;

import java.lang.annotation.Annotation;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.DynamicModule;

public class InjectionUtil {
    private Injector injector;
    
    public static InjectionUtil create(Class<? extends ClasspathScanner> scanner, String... packages){
	Injector startupInjector = Guice.createInjector(StartupModule.create(
	    scanner, packages));
	DynamicModule dynamicModule = startupInjector.getInstance(DynamicModule.class);
	
	InjectionUtil util = new InjectionUtil();
	util.injector = Guice.createInjector(dynamicModule);
	
	return util;
    }

    public <T> T lookup(Class<T> key) {
	return injector.getInstance(key);
    }
    
    public <T> T lookup(Class<T> key, String name) {
	return injector.getInstance(Key.get(key, Names.named(name)));
    }
    
    public <T> T lookup(Class<T> key, Class<? extends Annotation> annotation) {
	return injector.getInstance(Key.get(key, annotation));
    }
}
