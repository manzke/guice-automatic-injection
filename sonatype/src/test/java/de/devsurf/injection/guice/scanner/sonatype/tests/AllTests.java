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
package de.devsurf.injection.guice.scanner.sonatype.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.devsurf.injection.guice.scanner.sonatype.tests.autobind.AutobindTests;
import de.devsurf.injection.guice.scanner.sonatype.tests.autobind.bind.InterfaceAutobindTests;
import de.devsurf.injection.guice.scanner.sonatype.tests.autobind.multiple.MultibindTests;
import de.devsurf.injection.guice.scanner.sonatype.tests.autobind.names.NamedAutobindTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  AutobindTests.class,
  InterfaceAutobindTests.class,
  NamedAutobindTests.class,
  MultibindTests.class
})
public class AllTests {}
