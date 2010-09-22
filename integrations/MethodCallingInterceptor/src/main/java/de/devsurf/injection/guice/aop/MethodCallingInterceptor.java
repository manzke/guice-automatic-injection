package de.devsurf.injection.guice.aop;


import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import de.devsurf.injection.guice.aop.Interceptor.ClassMatcher;
import de.devsurf.injection.guice.aop.Interceptor.Intercept;
import de.devsurf.injection.guice.aop.Interceptor.Invoke;
import de.devsurf.injection.guice.aop.Interceptor.MethodMatcher;

@Interceptor
public class MethodCallingInterceptor{
    private Logger _logger = Logger.getLogger(MethodCallingInterceptor.class.getName());
    
    @Invoke
    public Object invoke(MethodInvocation invocation) throws Throwable {
	Object destination = invocation.getThis();
	StringBuilder logMessageBuilder = new StringBuilder(250);
	
	logMessageBuilder.append("Invoking Method \"");
	logMessageBuilder.append(invocation.getMethod().getName());
	logMessageBuilder.append("\" on ");
	logMessageBuilder.append(destination.getClass().getName());
	logMessageBuilder.append(" with Arguments: ");
	
	for(Object parameter : invocation.getArguments()){
	    logMessageBuilder.append(" \"");
	    logMessageBuilder.append(parameter.getClass().getSimpleName());
	    logMessageBuilder.append("\": ");
	    logMessageBuilder.append(parameter);
	}
	_logger.log(Level.SEVERE, logMessageBuilder.toString());

	return invocation.proceed();
    }

    @ClassMatcher
    public Matcher<? super Class<?>> getClassMatcher() {
	return Matchers.any();
    }

    @MethodMatcher
    public Matcher<? super Method> getMethodMatcher() {
	return Matchers.annotatedWith(Intercept.class);
    }

}
