package de.devsurf.injection.guice.asm.example.autobind.names;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.StartupModule;
import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.asm.VirtualClasspathReader;

public class ExampleApp 
{
    public static void main( String[] args ) throws IOException
    {
    	Injector injector = Guice.createInjector(new StartupModule(VirtualClasspathReader.class, "de.devsurf"));
    	DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
    	injector = injector.createChildInjector(dynamicModule);
    	System.out.println(injector.getInstance(Key.get(Example.class, Names.named("Example"))).sayHello());
    }
}
