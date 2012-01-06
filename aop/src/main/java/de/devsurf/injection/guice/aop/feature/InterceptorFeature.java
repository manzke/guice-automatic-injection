/**
 * Copyright (C) 2010 Daniel Manzke <daniel.manzke@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 */
package de.devsurf.injection.guice.aop.feature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.interceptor.Interceptor;

import de.devsurf.injection.guice.install.BindingStage;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import de.devsurf.injection.guice.aop.ClassMatcher;
import de.devsurf.injection.guice.aop.GuiceMethodInterceptor;
import de.devsurf.injection.guice.aop.Invoke;
import de.devsurf.injection.guice.aop.MethodMatcher;
import de.devsurf.injection.guice.scanner.features.BindingScannerFeature;

@Singleton
public class InterceptorFeature extends BindingScannerFeature {
	private Logger _logger = Logger.getLogger(InterceptorFeature.class.getName());

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
						"Skipping process(..) of \""
								+ annotatedClass
								+ "\", because an Exception occured while trying to invoke a Method of the found Intercepter.",
						e);
				return;
			}

			if (possibleInterceptor instanceof MethodInterceptor) {
				interceptor = (MethodInterceptor) possibleInterceptor;
			} else {
				if (methods.containsKey(Invoke.class)) {
					final Method method = methods.get(Invoke.class);
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes != null && parameterTypes.length == 1
							&& parameterTypes[0] == MethodInvocation.class) {
						interceptor = new MethodInterceptor() {
							@Override
							public Object invoke(MethodInvocation invocation) throws Throwable {
								return method.invoke(possibleInterceptor, invocation);
							}
						};
					} else {
						_logger
							.log(
								Level.WARNING,
								"Skipping \""
										+ annotatedClass
										+ "\", because the Parameter of the with @Invoke annotated Method \""
										+ method.getName()
										+ "\" doesn't match the expected one. "
										+ method.getName() + "(MethodInvocation invocation)");
						return;
					}
				} else {
					_logger.log(Level.WARNING, "Skipping \"" + annotatedClass
							+ "\" is either Child of \""
							+ GuiceMethodInterceptor.class.getName() + "\" / \""
							+ MethodInterceptor.class.getName()
							+ "\" nor has a Method annotated with \"" + Invoke.class.getName()
							+ "\"");
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