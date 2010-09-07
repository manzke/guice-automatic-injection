package de.devsurf.injection.guice;

import java.lang.annotation.Annotation;

import com.google.inject.name.Named;

public class NamedInterop {
    public static String getName(Annotation annotation){
	if(annotation instanceof Named){
	    return ((Named)annotation).value();
	}else if(annotation instanceof javax.inject.Named){
	    return ((javax.inject.Named)annotation).value();
	}
	return "";
    }
}
