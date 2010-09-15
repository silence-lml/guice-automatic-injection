package de.devsurf.injection.guice.integrations.guicyfruit.jndi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.naming.Context;
import javax.naming.NamingException;

import org.guiceyfruit.Injectors;
import org.guiceyfruit.jndi.GuiceInitialContextFactory;
import org.guiceyfruit.jndi.JndiBindings;
import org.guiceyfruit.jndi.internal.JndiContext;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scopes;
import com.google.inject.util.Modules;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.StartupModule;

public class GuicyInitialContextFactory extends GuiceInitialContextFactory{
    public GuicyInitialContextFactory() {
	super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Context getInitialContext(final Hashtable environment) throws NamingException {
	try {
	    String classpathScannerClass = (String) environment.get("guice.classpath.scanner");
	    if (classpathScannerClass == null || classpathScannerClass.length() == 0) {
		classpathScannerClass = "de.devsurf.injection.guice.scanner.asm.VirtualClasspathReader";
	    }
	    Class<ClasspathScanner> scannerClass = (Class<ClasspathScanner>) Class
		.forName(classpathScannerClass.trim());

	    String classpathPackages = (String) environment.get("guice.classpath.packages");
	    if (classpathPackages == null || classpathPackages.length() == 0) {
		classpathPackages = "com;de;org;net";
	    }
	    List<String> packages = new ArrayList<String>();

	    StringTokenizer tok = new StringTokenizer(classpathPackages.trim(), ";");
	    while (tok.hasMoreElements()) {
		packages.add(tok.nextToken().trim());
	    }

	    StartupModule startupModule = StartupModule.create(scannerClass, packages
		.toArray(new String[packages.size()]));
	    Injector injector = Guice.createInjector(startupModule);
	    // FIXME we create a new Injector. We should use
	    // createChildInjector, but
	    // this is not recognizing any bindListeners, which are bound in the
	    // Child
	    // Modules.

	    injector = Injectors.createInjector(environment, Modules.combine(startupModule,
		injector.getInstance(DynamicModule.class)), new AbstractModule() {
		protected void configure() {
		    bind(Context.class).toProvider(new Provider<Context>() {
			@Inject
			Injector injector;

			public Context get() {
			    JndiContext context = new JndiContext(environment);
			    Properties jndiNames = createJndiNamesProperties(environment);
			    try {
				JndiBindings.bindInjectorAndBindings(context, injector, jndiNames);
				return context;
			    } catch (NamingException e) {
				throw new ProvisionException(
				    "Failed to create JNDI bindings. Reason: " + e, e);
			    }
			}
		    }).in(Scopes.SINGLETON);
		}
	    });
	    return injector.getInstance(Context.class);
	} catch (Exception e) {
	    NamingException exception = new NamingException(e.getMessage());
	    exception.initCause(e);
	    throw exception;
	}
    }

    @SuppressWarnings("unchecked")
    private Properties createJndiNamesProperties(Hashtable environment) {
	Set<Map.Entry> set = environment.entrySet();
	Properties answer = new Properties();
	for (Entry entry : set) {
	    String key = entry.getKey().toString();
	    if (key.startsWith(NAME_PREFIX)) {
		String name = key.substring(NAME_PREFIX.length());
		Object value = entry.getValue();
		answer.put(name, value);
	    }
	}
	return answer;
    }
}