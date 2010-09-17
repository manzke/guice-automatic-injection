package de.devsurf.injection.guice.scanner.asm.example.rocoto.automodule;

import de.devsurf.injection.guice.integrations.configuration.Configuration;
import de.devsurf.injection.guice.integrations.configuration.Configuration.PathType;

@Configuration(path="/configuration.properties", pathType=PathType.CLASSPATH)
public interface ExampleConfiguration {

}
