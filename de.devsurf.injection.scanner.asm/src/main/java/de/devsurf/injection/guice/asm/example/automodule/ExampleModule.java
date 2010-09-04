package de.devsurf.injection.guice.asm.example.automodule;

import com.google.inject.AbstractModule;

import de.devsurf.injection.guice.annotations.GuiceModule;

@GuiceModule
public class ExampleModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(Example.class).to(ExampleImpl.class);
	}
}
