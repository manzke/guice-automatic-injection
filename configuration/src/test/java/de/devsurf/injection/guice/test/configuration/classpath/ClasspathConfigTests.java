package de.devsurf.injection.guice.test.configuration.classpath;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.configuration.PropertiesListener;
import de.devsurf.injection.guice.configuration.Configuration.PathType;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.asm.VirtualClasspathReader;

public class ClasspathConfigTests {
    @Test
    public void createDynamicModule() {
	StartupModule startup = StartupModule.create(VirtualClasspathReader.class,
	    ClasspathConfigTests.class.getPackage().getName());
	startup.addFeature(PropertiesListener.class);

	Injector injector = Guice.createInjector(startup);
	assertNotNull(injector);

	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);

	injector = Guice.createInjector(dynamicModule);
	assertNotNull(injector);
    }

    @Test
    public void createPListConfiguration() {
	StartupModule startup = StartupModule.create(VirtualClasspathReader.class,
	    ClasspathConfigTests.class.getPackage().getName());
	startup.addFeature(PropertiesListener.class);

	Injector injector = Guice.createInjector(startup);
	assertNotNull(injector);

	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
	assertNotNull(dynamicModule);

	injector = Guice.createInjector(dynamicModule);
	assertNotNull(injector);

	TestInterface instance = injector.getInstance(TestInterface.class);
	Assert.assertTrue("sayHello() - yeahh!!".equals(instance.sayHello()));
    }

    @de.devsurf.injection.guice.configuration.Configuration(name = "config", path = "/configuration.properties", pathType = PathType.CLASSPATH)
    public interface TestConfiguration {
    }

    public static interface TestInterface {
	String sayHello();
    }

    @AutoBind
    public static class TestImplementations implements TestInterface {
	@Inject
	@Named("config")
	private Properties config;

	@Override
	public String sayHello() {
	    return "sayHello() - " + config.getProperty("message");
	}
    }
}
