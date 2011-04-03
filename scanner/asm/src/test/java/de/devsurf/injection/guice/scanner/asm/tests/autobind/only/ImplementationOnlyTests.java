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
package de.devsurf.injection.guice.scanner.asm.tests.autobind.only;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.inject.Named;

import org.junit.Test;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.annotations.To;
import de.devsurf.injection.guice.annotations.To.Type;
import de.devsurf.injection.guice.jsr330.Names;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;

public class ImplementationOnlyTests {
	@Test
	public void createDynamicModule() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(ImplementationOnlyTests.class)));
		assertNotNull(injector);
	}

	@Test
	public void testWithWrongPackage() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create("java")));
		assertNotNull(injector);

		TestInterfaceImplementation testInstance;
		try {
			testInstance = injector.getInstance(Key.get(TestInterfaceImplementation.class, Names
				.named("testname")));
			fail("The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "
					+ (testInstance == null));
		} catch (ConfigurationException e) {
			// ok
		}
	}
	
 	@Test
	public void createTestInterface() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(ImplementationOnlyTests.class)));
		assertNotNull(injector);

		try {
			TestInterfaceImplementation testInstance = injector.getInstance(Key.get(
				TestInterfaceImplementation.class, Names.named("testname")));
			fail("Named Bindings for Implementation only is not supported yet! "
					+ (testInstance != null));
		} catch (Exception e) {
			// ignore
		}
	}

	@Bind(value=@Named("testname"), to=@To(value=Type.IMPLEMENTATION))
	public static class TestInterfaceImplementation {
		public static final String TEST = "test";
		public static final String EVENT = "event";

		public String sayHello() {
			return TEST;
		}

		public String fireEvent() {
			return EVENT;
		}
	}
}
