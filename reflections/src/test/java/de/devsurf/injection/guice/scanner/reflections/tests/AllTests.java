package de.devsurf.injection.guice.scanner.reflections.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.devsurf.injection.guice.scanner.reflections.tests.autobind.AutobindTests;
import de.devsurf.injection.guice.scanner.reflections.tests.autobind.bind.InterfaceAutobindTests;
import de.devsurf.injection.guice.scanner.reflections.tests.autobind.names.NamedAutobindTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  AutobindTests.class,
  InterfaceAutobindTests.class,
  NamedAutobindTests.class,
})
public class AllTests {}
