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
package de.devsurf.injection.guice.aop.example.interceptor;

import java.lang.reflect.Method;

import javax.interceptor.Interceptor;

import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;

import de.devsurf.injection.guice.aop.ClassMatcher;
import de.devsurf.injection.guice.aop.Intercept;
import de.devsurf.injection.guice.aop.Invoke;
import de.devsurf.injection.guice.aop.MethodMatcher;

@Interceptor
public class InvalidMethodInterceptor {

	@Invoke
	public Object invoke(MethodInvocation invocation, Object obj) throws Throwable {
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
