package de.devsurf.injection.guice.scanner.asm.example.autobind.marker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

@Retention(RetentionPolicy.RUNTIME)
@Qualifier
@Target( { ElementType.TYPE })
public @interface SecondMarker {

}
