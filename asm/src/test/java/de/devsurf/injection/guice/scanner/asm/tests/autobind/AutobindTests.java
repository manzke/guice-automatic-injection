package de.devsurf.injection.guice.scanner.asm.tests.autobind;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.asm.VirtualClasspathReader;

public class AutobindTests {
    @Test
    public void createDynamicModule(){
	Injector injector = Guice.createInjector(StartupModule.create(VirtualClasspathReader.class, AutobindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
    }

    @Test
    public void testWithWrongPackage(){
	Injector injector = Guice.createInjector(StartupModule.create(VirtualClasspathReader.class, "java"));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	TestInterface testInstance;
	try {
	    testInstance = injector.getInstance(TestInterface.class);
	    fail("The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "+(testInstance == null));
	} catch (ConfigurationException e) {
	    //ok
	}
    }
    
    @Test
    public void createTestInterface(){
	Injector injector = Guice.createInjector(StartupModule.create(VirtualClasspathReader.class, AutobindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	TestInterface testInstance = injector.getInstance(TestInterface.class);
	assertNotNull(testInstance);
	assertTrue(testInstance.sayHello().equals(TestInterfaceImplementation.TEST));
	assertTrue(testInstance instanceof TestInterfaceImplementation);
	assertTrue(testInstance instanceof SecondTestInterface);
    }
    
    @Test
    public void createSecondTestInterface(){
	Injector injector = Guice.createInjector(StartupModule.create(VirtualClasspathReader.class, AutobindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	SecondTestInterface sameInstance = injector.getInstance(SecondTestInterface.class);
	assertNotNull(sameInstance);
	assertTrue(sameInstance.fireEvent().equals(TestInterfaceImplementation.EVENT));
	assertTrue(sameInstance instanceof TestInterfaceImplementation);
	assertTrue(sameInstance instanceof TestInterface);
    }
    
    @Test
    public void createAllInterfaces(){
	Injector injector = Guice.createInjector(StartupModule.create(VirtualClasspathReader.class, AutobindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	TestInterface testInstance = injector.getInstance(TestInterface.class);
	assertNotNull(testInstance);
	assertTrue(testInstance.sayHello().equals(TestInterfaceImplementation.TEST));
	assertTrue(testInstance instanceof TestInterfaceImplementation);
	assertTrue(testInstance instanceof SecondTestInterface);
	
	SecondTestInterface sameInstance = injector.getInstance(SecondTestInterface.class);
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
}
