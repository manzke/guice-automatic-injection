package de.devsurf.injection.guice.scanner.reflections.tests.autobind.multiple;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.annotations.MultiBinding;
import de.devsurf.injection.guice.scanner.reflections.ReflectionsScanner;

public class MultibindTests {
    @Test
    public void createDynamicModule(){
	Injector injector = Guice.createInjector(StartupModule.create(ReflectionsScanner.class, MultibindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
    }
    
    @Test
    public void testWithWrongPackage1(){
	Injector injector = Guice.createInjector(StartupModule.create(ReflectionsScanner.class, "java"));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	try {
	    FirstContainer container = injector.getInstance(FirstContainer.class);
	    fail("The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "+(container == null));
	} catch (ConfigurationException e) {
	    //ok
	}
    }

    @Test
    public void testWithWrongPackage2(){
	Injector injector = Guice.createInjector(StartupModule.create(ReflectionsScanner.class, "java"));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	try {
	    SecondContainer container = injector.getInstance(SecondContainer.class);
	    fail("The Scanner scanned the wrong package, so no Implementation should be bound to this Interface. Instance null? "+(container == null));
	} catch (ConfigurationException e) {
	    //ok
	}
    }
    
    @Test
    public void createFirstContainer(){
	Injector injector = Guice.createInjector(StartupModule.create(ReflectionsScanner.class, MultibindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	FirstContainer container = injector.getInstance(FirstContainer.class);
	assertNotNull(container);
	assertTrue(container.size() == 2);
	for(FirstInterface obj : container.get()){
	    assertTrue(obj instanceof FirstInterface);
	    assertTrue(obj instanceof SecondInterface);
	    assertTrue(obj instanceof FirstImplementation || obj instanceof SecondImplementation);
	}
    }
    
    @Test
    public void createSecondTestInterface(){
	Injector injector = Guice.createInjector(StartupModule.create(ReflectionsScanner.class, MultibindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	SecondContainer container = injector.getInstance(SecondContainer.class);
	assertNotNull(container);
	assertTrue(container.size() == 2);
	for(SecondInterface obj : container.get()){
	    assertTrue(obj instanceof FirstInterface);
	    assertTrue(obj instanceof SecondInterface);
	    assertTrue(obj instanceof FirstImplementation || obj instanceof SecondImplementation);
	}
    }
    
    @Test
    public void createAllInterfaces(){
	Injector injector = Guice.createInjector(StartupModule.create(ReflectionsScanner.class, MultibindTests.class.getPackage().getName()));
	assertNotNull(injector);
	
	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);
	
	injector = injector.createChildInjector(dynamicModule);
	assertNotNull(injector);
	
	FirstContainer firstContainer = injector.getInstance(FirstContainer.class);
	assertNotNull(firstContainer);
	assertTrue(firstContainer.size() == 2);
	for(FirstInterface obj : firstContainer.get()){
	    assertTrue(obj instanceof FirstInterface);
	    assertTrue(obj instanceof SecondInterface);
	    assertTrue(obj instanceof FirstImplementation || obj instanceof SecondImplementation);
	}
	
	SecondContainer secondContainer = injector.getInstance(SecondContainer.class);
	assertNotNull(secondContainer);
	assertTrue(secondContainer.size() == 2);
	for(SecondInterface obj : secondContainer.get()){
	    assertTrue(obj instanceof FirstInterface);
	    assertTrue(obj instanceof SecondInterface);
	    assertTrue(obj instanceof FirstImplementation || obj instanceof SecondImplementation);
	}
    }
    
    public static interface FirstInterface{
	String sayHello();
    }
    
    public static interface SecondInterface{
	String fireEvent();
    }
    
    public static class FirstContainer{
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
    
    public static class SecondContainer{
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
    
    @AutoBind
    @MultiBinding
    public static class FirstImplementation implements FirstInterface, SecondInterface{
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
    
    @AutoBind
    @MultiBinding
    public static class SecondImplementation implements FirstInterface, SecondInterface{
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
