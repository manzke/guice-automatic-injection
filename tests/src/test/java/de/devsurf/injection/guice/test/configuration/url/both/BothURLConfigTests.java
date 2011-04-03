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
package de.devsurf.injection.guice.test.configuration.url.both;

import static org.junit.Assert.assertNotNull;

import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;

import junit.framework.Assert;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.configuration.Configuration;
import de.devsurf.injection.guice.configuration.Configuration.Type;
import de.devsurf.injection.guice.configuration.PathConfig;
import de.devsurf.injection.guice.configuration.PathConfig.PathType;
import de.devsurf.injection.guice.configuration.features.ConfigurationFeature;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;

public class BothURLConfigTests {
	@Test
	public void createDynamicModule() {
		StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(BothURLConfigTests.class));
		startup.addFeature(ConfigurationFeature.class);

		Injector injector = Guice.createInjector(startup);
		assertNotNull(injector);
	}

	@Test
	public void createPListConfiguration() {
		StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
			PackageFilter.create(BothURLConfigTests.class));
		startup.addFeature(ConfigurationFeature.class);

		Injector injector = Guice.createInjector(startup);
		assertNotNull(injector);

		TestInterface instance = injector.getInstance(TestInterface.class);
		Assert.assertTrue("sayHello() - yeahh!!".equals(instance.sayHello()));
	}

	@Configuration(name = @Named("config"), location = @PathConfig(value = "http://devsurf.de/guice/configuration.properties", type = PathType.URL), type=Type.BOTH)
	public interface TestConfiguration {
	}

	public static interface TestInterface {
		String sayHello();
	}

	@Bind
	public static class TestImplementations implements TestInterface {
		@Inject
		@Named("message")
		private String message;
		
		@Inject
		@Named("config")
		private Properties config;

		@Override
		public String sayHello() {
			String msg = config.getProperty("message","");
			if(msg.equals(message)){
				return "sayHello() - " + message;	
			}
			return "message in Properties-Map isn't equal to direct injected Message";
		}
	}
}
