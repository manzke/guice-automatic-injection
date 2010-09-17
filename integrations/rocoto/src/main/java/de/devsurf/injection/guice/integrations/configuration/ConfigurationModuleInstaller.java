package de.devsurf.injection.guice.integrations.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.googlecode.rocoto.simpleconfig.SimpleConfigurationModule;

import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;

@GuiceModule(stage=BindingStage.BUILD)
public class ConfigurationModuleInstaller extends AbstractModule{
    @Inject 
    private SimpleConfigurationModule module;
    
    @Override
    protected void configure() {
	binder().install(module);
    }
}
