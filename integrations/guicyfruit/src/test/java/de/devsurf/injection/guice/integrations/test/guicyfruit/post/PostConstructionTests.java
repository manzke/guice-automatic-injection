package de.devsurf.injection.guice.integrations.test.guicyfruit.post;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.integrations.guicyfruit.JSR250Module;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.asm.VirtualClasspathReader;

public class PostConstructionTests {
    private static ThreadLocal<Boolean> called = new ThreadLocal<Boolean>();
    
    @Test
    public void createDynamicModule() {
	StartupModule startup = StartupModule.create(VirtualClasspathReader.class,
	    PostConstructionTests.class.getPackage().getName(), JSR250Module.class.getPackage().getName());

	Injector injector = Guice.createInjector(startup);
	assertNotNull(injector);

	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);

	injector = Guice.createInjector(dynamicModule);
	assertNotNull(injector);
    }

    @Test
    public void createInheritedInterceptor() {
	called.set(false);
	
	StartupModule startup = StartupModule.create(VirtualClasspathReader.class,
	    PostConstructionTests.class.getPackage().getName(), JSR250Module.class.getPackage().getName());

	Injector injector = Guice.createInjector(startup);
	assertNotNull(injector);

	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);

	injector = Guice.createInjector(dynamicModule);
	assertNotNull(injector);

	TestInterface instance = injector.getInstance(TestInterface.class);
	instance.sayHello();
	
	assertTrue("@PostConstruction was not evaluated and Method was not invoked", called.get());
    }
    
    public static interface TestInterface {
	String sayHello();
    }

    @AutoBind
    public static class TestImplementation implements TestInterface {
	@PostConstruct
	public void inform() {
	    called.set(true);
	}
	
	public void cancel(){
	    Assert.fail("Should not be invoked.");
	}

	@Override
	public String sayHello() {
	    return "Good Morning!";
	}

    }
}
