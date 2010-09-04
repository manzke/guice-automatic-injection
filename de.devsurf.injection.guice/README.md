#Automatic-Injection

This is the Core module which defines the Interfaces used to create Classpath Scanner implementations.
Existing implementations are Reflections/Javassit, a Sonatype-Extension and my own implementation based 
on ASM.

##Example
Base for our Examples is the Example interface...

	public interface Example {
		String sayHello();
	}

...and our Example-Application...

	public class ExampleApp {
		public static void main( String[] args ) throws IOException {
			Injector injector = Guice.createInjector(new StartupModule(VirtualClasspathReader.class, "de.devsurf"));
			DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
			injector = injector.createChildInjector(dynamicModule);

			System.out.println(injector.getInstance(Example.class).sayHello());
		}
	}

...our Example Application also shows, how to use the automatic Injection.

First of all you have to create a StartupModule and pass the Class of the ClasspathScanner you want to use. As 
a second Parameter you can specify which Packages should be scanned. Not all Scanner will support this feature,
so it can be, that the Packages get ignored. 

###AutoBind-Example
To use our AutoBind-Annotation you just have to annotate our Implementation...

	@AutoBind
	public class ExampleImpl implements Example {
		@Override
		public String sayHello() {
			return "yeahhh!!!";
		}
	}

...so this Class will be registered by our Startup/Scanner-Module and will be bound to all inherited interfaces. If you want that your Class should also be named, 
you have to set the name-Attribute...

	@AutoBind(name="impl")

...this will create a Key for the Binding. You can also overwrite the interfaces it should be bound to...

	@AutoBind(bind={Example.class})

...by passing the Interfaces to the bind()-Attribute.

###GuiceModule-Example
If have enough to register every Guice-Module by your own, you just can annotate it with the GuiceModule-Annotation and the Startup/Scanner-Module will install it.

	@GuiceModule
	public class ExampleModule extends AbstractModule {
		@Override
		protected void configure() {
			bind(Example.class).to(ExampleImpl.class);
		}
	}

