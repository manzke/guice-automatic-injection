package de.devsurf.injection.guice.scanner.asm.tests.autobind.provider;

import javax.inject.Inject;
import javax.inject.Named;

public class Container {
	@Inject @Named("mode")
	private Mode mode;
	
	public Mode get(){
		return mode;
	}
}
