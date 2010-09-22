/**
 * 
 */
package de.devsurf.injection.guice.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.aopalliance.intercept.MethodInterceptor;

import com.google.inject.matcher.Matcher;

public abstract class GuiceMethodInterceptor implements MethodInterceptor {
    public static Type CLASS_MATCHER_TYPE;
    public static Type METHOD_MATCHER_TYPE;

    static {
        try {
    	CLASS_MATCHER_TYPE = GuiceMethodInterceptor.class.getMethod("getClassMatcher",
    	    new Class<?>[0]).getGenericReturnType();
    	METHOD_MATCHER_TYPE = GuiceMethodInterceptor.class.getMethod("getMethodMatcher",
    	    new Class<?>[0]).getGenericReturnType();
        } catch (Exception e) {
    	// ignore
        }
    }

    public abstract Matcher<? super Class<?>> getClassMatcher();

    public abstract Matcher<? super Method> getMethodMatcher();
}