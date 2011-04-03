package de.devsurf.injection.guice.jsr330;

import javax.inject.Named;

public class Names {

	  private Names() {}

	  /**
	   * Creates a {@link Named} annotation with {@code name} as the value.
	   */
	  public static Named named(String name) {
	    return new NamedImpl(name);
	  }
}