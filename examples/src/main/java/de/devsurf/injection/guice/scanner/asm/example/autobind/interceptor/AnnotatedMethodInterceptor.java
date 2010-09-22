package de.devsurf.injection.guice.scanner.asm.example.autobind.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import de.devsurf.injection.guice.aop.Intercept;
import de.devsurf.injection.guice.aop.Interceptor;
import de.devsurf.injection.guice.aop.Interceptor.ClassMatcher;
import de.devsurf.injection.guice.aop.Interceptor.Invoke;
import de.devsurf.injection.guice.aop.Interceptor.MethodMatcher;

@Interceptor
public class AnnotatedMethodInterceptor{

    @Invoke
    public Object invoke(MethodInvocation invocation) throws Throwable {
	System.out.println(AnnotatedMethodInterceptor.class.getSimpleName()+" - Trying to invoke: "+invocation.getMethod().getName());
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
