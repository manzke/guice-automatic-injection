/**
 * 
 */
package de.devsurf.injection.guice.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import de.devsurf.injection.guice.scanner.GuiceAnnotationListener;
import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;

@Singleton
public class InterceptorListener extends GuiceAnnotationListener {
    private Logger _logger = Logger.getLogger(InterceptorListener.class.getName());

    @Override
    public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
        if (annotations.containsKey(Interceptor.class.getName())) {
    	return BindingStage.BOOT;
        }
        return BindingStage.IGNORE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
        MethodInterceptor interceptor;
        final Object possibleInterceptor = injector.getInstance(annotatedClass);

        Matcher<? super Class<?>> classMatcher = null;
        Matcher<? super Method> methodMatcher = null;
        if (possibleInterceptor instanceof GuiceMethodInterceptor) {
    	interceptor = (MethodInterceptor) possibleInterceptor;
    	GuiceMethodInterceptor guiceInterceptor = (GuiceMethodInterceptor) interceptor;
    	classMatcher = guiceInterceptor.getClassMatcher();
    	methodMatcher = guiceInterceptor.getMethodMatcher();
        } else {
    	Method[] declaredMethods = annotatedClass.getDeclaredMethods();
    	Map<Class<? extends Annotation>, Method> methods = new HashMap<Class<? extends Annotation>, Method>();

    	for (Method method : declaredMethods) {
    	    Annotation[] methodAnnotations = method.getAnnotations();
    	    for (Annotation methodAnnotation : methodAnnotations) {
    		methods.put(methodAnnotation.annotationType(), method);
    	    }
    	}
    	try {
    	    if (methods.containsKey(ClassMatcher.class)) {
    		Method method = methods.get(ClassMatcher.class);
    		Type genericReturnType = method.getGenericReturnType();
    		if (GuiceMethodInterceptor.CLASS_MATCHER_TYPE.equals(genericReturnType)) {
    		    classMatcher = (Matcher<? super Class<?>>) method.invoke(
    			possibleInterceptor, new Object[0]);
    		} else {
    		    _logger.log(Level.WARNING,
    			"Return Type of the annotated @ClassMatcher-Method, does not return: "
    				+ GuiceMethodInterceptor.CLASS_MATCHER_TYPE
    				+ " instead it returns " + genericReturnType);
    		}
    	    }

    	    if (methods.containsKey(MethodMatcher.class)) {
    		Method method = methods.get(MethodMatcher.class);
    		Type genericReturnType = method.getGenericReturnType();
    		if (GuiceMethodInterceptor.METHOD_MATCHER_TYPE.equals(genericReturnType)) {
    		    methodMatcher = (Matcher<? super Method>) method.invoke(
    			possibleInterceptor, new Object[0]);
    		} else {
    		    _logger.log(Level.WARNING,
    			"Return Type of the annotated @MethodMatcher-Method, does not return: "
    				+ GuiceMethodInterceptor.METHOD_MATCHER_TYPE
    				+ " instead it returns " + genericReturnType);
    		}
    	    }
    	} catch (Exception e) {
    	    _logger
    		.log(
    		    Level.WARNING,
    		    "Skipping process(..) of "+annotatedClass+", because an Exception occured while trying to invoke a Method of the found Intercepter: "
    			    + annotatedClass.getName(), e);
    	    return;
    	}

    	if (possibleInterceptor instanceof MethodInterceptor) {
    	    interceptor = (MethodInterceptor) possibleInterceptor;
    	} else {
    	    if (methods.containsKey(Invoke.class)) {
    		final Method method = methods.get(Invoke.class);
    		interceptor = new MethodInterceptor() {
    		    @Override
    		    public Object invoke(MethodInvocation invocation) throws Throwable {
    			return method.invoke(possibleInterceptor, invocation);
    		    }
    		};
    	    }else{
    		_logger.log(Level.WARNING, "Skipping "+annotatedClass+" is either Child of "+GuiceMethodInterceptor.class.getName()+" / "+MethodInterceptor.class.getName()+" nor has a Method annotated with "+Invoke.class.getName());
    		return;
    	    }
    	}
        }

        if (classMatcher == null) {
    	classMatcher = Matchers.any();
        }

        if (methodMatcher == null) {
    	methodMatcher = Matchers.any();
        }

        _binder.bindInterceptor(classMatcher, methodMatcher, interceptor);
    }
}