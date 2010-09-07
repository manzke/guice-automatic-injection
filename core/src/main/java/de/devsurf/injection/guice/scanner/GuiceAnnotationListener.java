package de.devsurf.injection.guice.scanner;

import com.google.inject.Binder;

/**
 * Default Implementation for Annotation Listeners, which should stay informed
 * abbout found annotated classes. Due the fact, that we need the Binder of the
 * Child Injector, it will be set at runtime by the {@link ScannerModule}.
 * 
 * @author Daniel Manzke
 * 
 */
public abstract class GuiceAnnotationListener implements AnnotationListener {
    protected Binder _binder;

    public void setBinder(Binder binder) {
	_binder = binder;
    }
}
