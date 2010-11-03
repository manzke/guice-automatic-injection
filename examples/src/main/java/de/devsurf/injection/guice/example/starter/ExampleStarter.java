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
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;

public class ExampleStarter {
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ExampleStartupModule(ASMClasspathScanner.class, PackageFilter.create("de.devsurf.injection.guice")));

		Key<Set<ExampleApplication>> key = Key.get(new TypeLiteral<Set<ExampleApplication>>() {
		});
		Set<ExampleApplication> apps = injector.getInstance(key);
		for (ExampleApplication app : apps) {
			System.out.println("Starting App: " + app.getClass().getName());
			app.run();
			System.out.println();
		}
		System.out.println("Run " + apps.size() + " Applications.");
	}
}
