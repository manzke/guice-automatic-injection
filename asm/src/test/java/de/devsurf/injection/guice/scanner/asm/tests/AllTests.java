package de.devsurf.injection.guice.scanner.asm.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.devsurf.injection.guice.scanner.asm.tests.autobind.AutobindTests;
import de.devsurf.injection.guice.scanner.asm.tests.autobind.bind.InterfaceAutobindTests;
import de.devsurf.injection.guice.scanner.asm.tests.autobind.names.NamedAutobindTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  AutobindTests.class,
  InterfaceAutobindTests.class,
  NamedAutobindTests.class,
})
public class AllTests {}
