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
package de.devsurf.injection.guice.test.aop;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.devsurf.injection.guice.test.aop.annoherited.AnnoheritedInterceptorTests;
import de.devsurf.injection.guice.test.aop.annotated.AnnotatedInterceptorTests;
import de.devsurf.injection.guice.test.aop.inherited.InheritedInterceptorTests;
import de.devsurf.injection.guice.test.aop.invalid.InvalidInterceptorTests;

@RunWith(Suite.class)
@Suite.SuiteClasses( { AnnoheritedInterceptorTests.class, AnnotatedInterceptorTests.class,
		InheritedInterceptorTests.class, InvalidInterceptorTests.class })
public class AllTests {
}
