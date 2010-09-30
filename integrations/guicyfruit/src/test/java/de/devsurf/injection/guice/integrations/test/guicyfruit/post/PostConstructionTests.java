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
package de.devsurf.injection.guice.integrations.test.guicyfruit.post;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.devsurf.injection.guice.integrations.guicyfruit.JSR250Module;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.annotations.AutoBind;
import de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner;

public class PostConstructionTests {
    private static ThreadLocal<Boolean> called = new ThreadLocal<Boolean>();
    
    @Test
    public void createDynamicModule() {
	StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
	    PostConstructionTests.class.getPackage().getName(), JSR250Module.class.getPackage().getName());

	Injector injector = Guice.createInjector(startup);
	assertNotNull(injector);
    }

    @Test
    public void createInheritedInterceptor() {
	called.set(false);
	
	StartupModule startup = StartupModule.create(ASMClasspathScanner.class,
	    PostConstructionTests.class.getPackage().getName(), JSR250Module.class.getPackage().getName());

	Injector injector = Guice.createInjector(startup);
	assertNotNull(injector);

	TestInterface instance = injector.getInstance(TestInterface.class);
	instance.sayHello();
	
	assertTrue("@PostConstruction was not evaluated and Method was not invoked", called.get());
    }
    
    public static interface TestInterface {
	String sayHello();
    }

    @AutoBind
    public static class TestImplementation implements TestInterface {
	@PostConstruct
	public void inform() {
	    called.set(true);
	}
	
	public void cancel(){
	    Assert.fail("Should not be invoked.");
	}

	@Override
	public String sayHello() {
	    return "Good Morning!";
	}

    }
}
