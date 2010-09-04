package de.devsurf.injection.guice;

import com.google.inject.Module;

/**
 * Due the fact that Guice is not able to bind Modules to {@link Module},
 * we have to bypass this issue. Due that fact we mark all Modules, which should
 * be injected and bind them.
 * 
 * @author Daniel Manzke
 *
 */
public interface DynamicModule extends Module{

}
