package de.devsurf.injection.guice.asm.example.autobind.names;

import de.devsurf.injection.guice.annotations.AutoBind;

@AutoBind(name="Example")
public class ExampleImpl implements Example {
	@Override
	public String sayHello() {
		return "yeahhh!!!";
		
	}
}
