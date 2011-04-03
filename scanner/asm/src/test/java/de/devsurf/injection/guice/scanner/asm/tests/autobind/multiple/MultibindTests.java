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
package de.devsurf.injection.guice.scanner.asm.tests.autobind.multiple;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Test;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;

public class MultibindTests {
	@Test
	public void createDynamicModule() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(MultibindTests.class)));
		assertNotNull(injector);
	}

	@Test
	public void testWithWrongPackage1() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create("java")));
		assertNotNull(injector);

		try {
			FirstContainer container = injector.getInstance(FirstContainer.class);
			fail("The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "
					+ (container == null));
		} catch (ConfigurationException e) {
			// ok
		}
	}

	@Test
	public void testWithWrongPackage2() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create("java")));
		assertNotNull(injector);

		try {
			SecondContainer container = injector.getInstance(SecondContainer.class);
			fail("The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "
					+ (container == null));
		} catch (ConfigurationException e) {
			// ok
		}
	}

	@Test
	public void createFirstContainer() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(MultibindTests.class)));
		assertNotNull(injector);

		FirstContainer container = injector.getInstance(FirstContainer.class);
		assertNotNull(container);
		assertTrue(container.size() == 2);
		for (FirstInterface obj : container.get()) {
			assertTrue(obj instanceof FirstInterface);
			assertTrue(obj instanceof SecondInterface);
			assertTrue(obj instanceof FirstImplementation || obj instanceof SecondImplementation);
		}
	}

	@Test
	public void createSecondTestInterface() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(MultibindTests.class)));
		assertNotNull(injector);

		SecondContainer container = injector.getInstance(SecondContainer.class);
		assertNotNull(container);
		assertTrue(container.size() == 2);
		for (SecondInterface obj : container.get()) {
			assertTrue(obj instanceof FirstInterface);
			assertTrue(obj instanceof SecondInterface);
			assertTrue(obj instanceof FirstImplementation || obj instanceof SecondImplementation);
		}
	}

	@Test
	public void createAllInterfaces() {
		Injector injector = Guice.createInjector(StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(MultibindTests.class)));
		assertNotNull(injector);

		FirstContainer firstContainer = injector.getInstance(FirstContainer.class);
		assertNotNull(firstContainer);
		assertTrue(firstContainer.size() == 2);
		for (FirstInterface obj : firstContainer.get()) {
			assertTrue(obj instanceof FirstInterface);
			assertTrue(obj instanceof SecondInterface);
			assertTrue(obj instanceof FirstImplementation || obj instanceof SecondImplementation);
		}

		SecondContainer secondContainer = injector.getInstance(SecondContainer.class);
		assertNotNull(secondContainer);
		assertTrue(secondContainer.size() == 2);
		for (SecondInterface obj : secondContainer.get()) {
			assertTrue(obj instanceof FirstInterface);
			assertTrue(obj instanceof SecondInterface);
			assertTrue(obj instanceof FirstImplementation || obj instanceof SecondImplementation);
		}
	}

	public static interface FirstInterface {
		String sayHello();
	}

	public static interface SecondInterface {
		String fireEvent();
	}

	public static class FirstContainer {
		private List<FirstInterface> implementations;

		@Inject
		public FirstContainer(Set<FirstInterface> implementations) {
			super();
			this.implementations = new ArrayList<FirstInterface>(implementations);
		}

		public int size() {
			return implementations.size();
		}

		public List<FirstInterface> get() {
			return implementations;
		}
	}

	public static class SecondContainer {
		private List<SecondInterface> implementations;

		@Inject
		public SecondContainer(Set<SecondInterface> implementations) {
			super();
			this.implementations = new ArrayList<SecondInterface>(implementations);
		}

		public int size() {
			return implementations.size();
		}

		public List<SecondInterface> get() {
			return implementations;
		}
	}

	@Bind(multiple = true)
	public static class FirstImplementation implements FirstInterface, SecondInterface {
		public static final String TEST = "test1";
		public static final String EVENT = "event1";

		@Override
		public String sayHello() {
			return TEST;
		}

		@Override
		public String fireEvent() {
			return EVENT;
		}
	}

	@Bind(multiple = true)
	public static class SecondImplementation implements FirstInterface, SecondInterface {
		public static final String TEST = "test2";
		public static final String EVENT = "event2";

		@Override
		public String sayHello() {
			return TEST;
		}

		@Override
		public String fireEvent() {
			return EVENT;
		}
	}
}
