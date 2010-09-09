/*******************************************************************************
 * Copyright 2010, Daniel Manzke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License.
 * 
 ******************************************************************************/
package de.devsurf.injection.guice.scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.scanner.StartupModule;
import de.devsurf.injection.guice.scanner.asm.VirtualClasspathReader;

public class Injection {
    private static final String packages = System.getProperty("injection-packages", "de.devsurf");
    private static final String delimiter = System.getProperty("injection-delimiter", ";");
    private static final Injector injector;

    static {
	List<String> tokens = new ArrayList<String>();
	StringTokenizer tok = new StringTokenizer(packages, delimiter);
	while (tok.hasMoreTokens()) {
	    tokens.add(tok.nextToken());
	}

	Injector startupInjector = Guice.createInjector(StartupModule.create(
	    VirtualClasspathReader.class, tokens.toArray(new String[tokens.size()])));
	DynamicModule dynamicModule = startupInjector.getInstance(DynamicModule.class);
	injector = startupInjector.createChildInjector(dynamicModule);
    }

    public static <T> T lookup(Class<T> key, String hint) throws NotFoundException {
	try {
	    return injector.getInstance(Key.get(key, Names.named(hint)));
	} catch (ConfigurationException e) {
	    throw new NotFoundException(e);
	}
    }

    public static <T> T lookup(Class<T> key) throws NotFoundException {
	try {
	    return injector.getInstance(key);
	} catch (ConfigurationException e) {
	    throw new NotFoundException(e);
	}
    }

    public static class NotFoundException extends Exception {

	private static final long serialVersionUID = -4018335662382124388L;

	public NotFoundException() {
	    super();
	}

	public NotFoundException(String message, Throwable cause) {
	    super(message, cause);
	}

	public NotFoundException(String message) {
	    super(message);
	}

	public NotFoundException(Throwable cause) {
	    super(cause);
	}

    }
}
