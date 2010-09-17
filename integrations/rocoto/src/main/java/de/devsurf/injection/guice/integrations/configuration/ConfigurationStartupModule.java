package de.devsurf.injection.guice.integrations.configuration;

import com.google.inject.multibindings.Multibinder;
import com.googlecode.rocoto.simpleconfig.SimpleConfigurationModule;

import de.devsurf.injection.guice.scanner.AnnotationListener;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.StartupModule.DefaultStartupModule;

public class ConfigurationStartupModule extends DefaultStartupModule {
    public ConfigurationStartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	super(scanner, packages);
    }

    @Override
    protected void bindAnnotationListeners() {
	super.bindAnnotationListeners();
	Multibinder<AnnotationListener> listeners = Multibinder.newSetBinder(binder(),
	    AnnotationListener.class);
	listeners.addBinding().to(Configuration.ConfigurationListener.class);
	bind(SimpleConfigurationModule.class).asEagerSingleton();
    }
}
