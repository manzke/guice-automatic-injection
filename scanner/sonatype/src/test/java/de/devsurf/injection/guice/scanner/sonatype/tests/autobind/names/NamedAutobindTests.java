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
package de.devsurf.injection.guice.scanner.sonatype.tests.autobind.names;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.inject.Named;

import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.ScannerFeature;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.sonatype.SonatypeScanner;

public class NamedAutobindTests {
    @Test
    public void createDynamicModule(){
	Injector injector = Guice.createInjector(new TestStartupModule(SonatypeScanner.class, NamedAutobindTests.class.getPackage().getName()));
	assertNotNull(injector);
    }
    
    @Test
    public void testWithWrongPackage(){
	Injector injector = Guice.createInjector(new TestStartupModule(SonatypeScanner.class, "java"));
	assertNotNull(injector);
	
	TestInterface testInstance;
	try {
	    testInstance = injector.getInstance(Key.get(TestInterface.class, Names.named("testname")));
	    fail("The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "+(testInstance == null));
	} catch (ConfigurationException e) {
	    //ok
	}
    }
    
    @Test
    public void createTestInterface(){
	Injector injector = Guice.createInjector(new TestStartupModule(SonatypeScanner.class, NamedAutobindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	TestInterface testInstance = injector.getInstance(Key.get(TestInterface.class, Names.named("testname")));
	assertNotNull(testInstance);
	assertTrue(testInstance.sayHello().equals(TestInterfaceImplementation.TEST));
	assertTrue(testInstance instanceof TestInterfaceImplementation);
	assertTrue(testInstance instanceof SecondTestInterface);
    }
    
    @Test
    public void createSecondTestInterface(){
	Injector injector = Guice.createInjector(new TestStartupModule(SonatypeScanner.class, NamedAutobindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	SecondTestInterface sameInstance = injector.getInstance(Key.get(SecondTestInterface.class, Names.named("testname")));
	assertNotNull(sameInstance);
	assertTrue(sameInstance.fireEvent().equals(TestInterfaceImplementation.EVENT));
	assertTrue(sameInstance instanceof TestInterfaceImplementation);
	assertTrue(sameInstance instanceof TestInterface);
    }
    
    @Test
    public void createAllInterfaces(){
	Injector injector = Guice.createInjector(new TestStartupModule(SonatypeScanner.class, NamedAutobindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	TestInterface testInstance = injector.getInstance(Key.get(TestInterface.class, Names.named("testname")));
	assertNotNull(testInstance);
	assertTrue(testInstance.sayHello().equals(TestInterfaceImplementation.TEST));
	assertTrue(testInstance instanceof TestInterfaceImplementation);
	assertTrue(testInstance instanceof SecondTestInterface);
	
	SecondTestInterface sameInstance = injector.getInstance(Key.get(SecondTestInterface.class, Names.named("testname")));
	assertNotNull(sameInstance);
	assertTrue(sameInstance.fireEvent().equals(TestInterfaceImplementation.EVENT));
	assertTrue(sameInstance instanceof TestInterfaceImplementation);
	assertTrue(testInstance instanceof TestInterface);
    }
    
    public static interface TestInterface{
	String sayHello();
    }
    
    public static interface SecondTestInterface{
	String fireEvent();
    }
    
    @AutoBind
    @Named("testname")
    public static class TestInterfaceImplementation implements TestInterface, SecondTestInterface{
	public static final String TEST = "test";
	public static final String EVENT = "event";
	
	@Override
	public String sayHello() {
	    return TEST;
	}
	
	@Override
	public String fireEvent() {
	    return EVENT;
	}
    }
    
    public static class TestStartupModule extends StartupModule{	
	public TestStartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	    super(scanner, packages);
	}

	@Override
	protected Multibinder<ScannerFeature> bindFeatures(Binder binder) {  
	    Multibinder<ScannerFeature> listeners = Multibinder.newSetBinder(binder,
		ScannerFeature.class);
	    listeners.addBinding().to(AutoBind.AutoBindListener.class);
	    
	    return listeners;
	}
    }
}
