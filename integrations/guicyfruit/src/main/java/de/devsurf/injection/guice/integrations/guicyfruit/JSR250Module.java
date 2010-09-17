package de.devsurf.injection.guice.integrations.guicyfruit;

import org.guiceyfruit.jsr250.Jsr250Module;

import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;
import de.devsurf.injection.guice.scanner.annotations.GuiceModule;

@GuiceModule(stage=BindingStage.BOOT)
public class JSR250Module extends Jsr250Module {
    public JSR250Module() {
	super();
    }
}
