package de.devsurf.injection.guice.reflections.example.automodule;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.StartupModule;
import de.devsurf.injection.guice.reflections.ReflectionsScanner;

public class ExampleApp 
{
    public static void main( String[] args ) throws IOException
    {
    	Injector injector = Guice.createInjector(new StartupModule(ReflectionsScanner.class, "de.devsurf"));
    	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
    	injector = injector.createChildInjector(dynamicModule);
    	
    	System.out.println(injector.getInstance(Example.class).sayHello());
    }
}
