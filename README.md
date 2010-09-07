#guice-automatic-injection

Google Guice-Extension for automatic Modules and Beans Binding.

##Blog-Entries
[Part 1](http://devsurf.wordpress.com/2010/09/06/google-guice-classpath-scanning-and-automatic-beans-binding-and-module-installation/)
[Part 2](https://devsurf.wordpress.com/2010/09/07/guice-automatic-injection-binding-listeners-parallel-scanning/)

[Ohloh.net](https://www.ohloh.net/p/guice-auto-injection)

[Github](git://github.com/manzke/guice-automatic-injection.git)
 

##Automatic-Injection

This is the Core module which defines the Interfaces used to create Classpath Scanner implementations and dynamic Binders.
Existing implementations are Reflections/Javassit, a Sonatype-Extension and my own implementation based 
on ASM.

###Example
Base for our Examples is the Example interface...

	public interface Example {
		String sayHello();
	}

...and our Example-Application...

	public class ExampleApp {
		public static void main( String[] args ) throws IOException {
			Injector injector = Guice.createInjector(StartupModule.create(VirtualClasspathReader.class, "de.devsurf"));
			DynamicModule dynamicModule = injector.getInstance(DynamicModule.class);
			injector = injector.createChildInjector(dynamicModule);

			System.out.println(injector.getInstance(Example.class).sayHello());
		}
	}

...which shows, how to use the automatic Injection.

First of all you have to create a StartupModule and pass the Class of the ClasspathScanner you want to use. As 
a second Parameter you can specify which Packages should be scanned. Not all Scanner will support this feature,
so it can be, that the Packages get ignored. 

####AutoBind-Example
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

####GuiceModule-Example
If you have enough to register every Guice-Module by your own, just annotate it with the @GuiceModule and the Startup/Scanner-Module will install it.

	@GuiceModule
	public class ExampleModule extends AbstractModule {
		@Override
		protected void configure() {
			bind(Example.class).to(ExampleImpl.class);
		}
	}

##TODOs:
- Multiple Bindings
	- it should be possible to bind multiple classes to one interface
	- use the Multibinding Extension
- Extend the Sonatype-Scanner to recognize the submitted packages
- Add parallel binding for Sonatype and pure Implementation


