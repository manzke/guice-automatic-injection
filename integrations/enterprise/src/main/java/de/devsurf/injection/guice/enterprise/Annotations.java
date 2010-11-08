package de.devsurf.injection.guice.enterprise;

import java.lang.annotation.Annotation;

import javax.interceptor.Interceptor;

public class Annotations extends de.devsurf.injection.guice.annotations.Annotations{
	public static Interceptor createInterceptor(){
		return new Interceptor() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Interceptor.class;
			}
		};
	}
}
