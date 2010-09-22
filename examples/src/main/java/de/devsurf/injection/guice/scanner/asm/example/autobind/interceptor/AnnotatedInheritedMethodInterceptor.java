package de.devsurf.injection.guice.scanner.asm.example.autobind.interceptor;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import de.devsurf.injection.guice.aop.Interceptor;
import de.devsurf.injection.guice.aop.Interceptor.ClassMatcher;
import de.devsurf.injection.guice.aop.Interceptor.Intercept;
import de.devsurf.injection.guice.aop.Interceptor.MethodMatcher;

@Interceptor
public class AnnotatedInheritedMethodInterceptor implements MethodInterceptor{

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
	System.out.println(AnnotatedInheritedMethodInterceptor.class.getSimpleName()+" - Trying to invoke: "+invocation.getMethod().getName());
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
