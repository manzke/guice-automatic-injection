package de.devsurf.injection.guice.scanner.sonatype.example.startupmodule;

import com.google.inject.CreationException;
import com.google.inject.multibindings.Multibinder;

import de.devsurf.injection.guice.scanner.AnnotationListener;
import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind.AutoBindListener;

/**
 * The {@link ExampleStartupModule} overwrites the
 * bindAnnotationListeners-Method, because our Example has several Classes
 * annotated with {@link AutoBind} and {@link GuiceModule}. Due the fact, that
 * our GuiceModule binds the {@link Example}-Interface to the
 * {@link ExampleImpl}-Class and the {@link AutoBindListener} too, we would get
 * a {@link CreationException}.
 * 
 * @author Daniel Manzke
 * 
 */
public class ExampleStartupModule extends StartupModule {

    public ExampleStartupModule(Class<? extends ClasspathScanner> scanner, String... packages) {
	super(scanner, packages);
    }

    @Override
    protected void bindAnnotationListeners() {
	Multibinder<AnnotationListener> listeners = Multibinder.newSetBinder(binder(),
	    AnnotationListener.class);
	listeners.addBinding().to(GuiceModule.GuiceModuleListener.class);
    }

}
