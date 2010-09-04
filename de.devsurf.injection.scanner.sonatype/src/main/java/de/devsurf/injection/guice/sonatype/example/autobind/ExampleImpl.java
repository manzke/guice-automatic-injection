package de.devsurf.injection.guice.sonatype.example.autobind;

import de.devsurf.injection.guice.annotations.AutoBind;

@AutoBind
public class ExampleImpl implements Example {
	@Override
	public String sayHello() {
		return "yeahhh!!!";
	}
}
