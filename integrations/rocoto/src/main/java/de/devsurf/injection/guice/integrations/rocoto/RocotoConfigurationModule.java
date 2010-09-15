package de.devsurf.injection.guice.integrations.rocoto;

import com.google.inject.AbstractModule;
import com.googlecode.rocoto.simpleconfig.SimpleConfigurationModule;

import de.devsurf.injection.guice.scanner.annotations.GuiceModule;

@GuiceModule
public class RocotoConfigurationModule extends AbstractModule{
    @Override
    protected void configure() {
	SimpleConfigurationModule module = new SimpleConfigurationModule();
	module.addProperties("/configuration.properties");
	binder().install(module);        
    }
}
