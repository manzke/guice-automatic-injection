package de.devsurf.injection.guice.asm.example.autobind;

import de.devsurf.injection.guice.annotations.AutoBind;

@AutoBind
public class ExampleImpl implements Example {
	@Override
	public String sayHello() {
		return "yeahhh!!!";
	}
}
