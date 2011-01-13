package de.devsurf.injection.guice.scanner.asm.tests.autobind.startconfig;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Container {
	@Inject @Named("mode")
	private Mode mode;
	
	public Mode get(){
		return mode;
	}
}
