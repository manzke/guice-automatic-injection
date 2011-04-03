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
package de.devsurf.injection.guice.integrations.guicyfruit.jndi;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.naming.NamingException;

import org.guiceyfruit.Injectors;
import org.guiceyfruit.jndi.GuiceInitialContextFactory;
import org.guiceyfruit.jndi.JndiBindings;
import org.guiceyfruit.jndi.internal.JndiContext;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.Scopes;

import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.StartupModule;

/**
 * ContextFactory-Implementation which provides JNDI with a Guice-Injector.
 * 
 * @author Daniel Manzke
 * 
 */
public class GuicyInitialContextFactory extends GuiceInitialContextFactory {
	public GuicyInitialContextFactory() {
		super();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Context getInitialContext(final Hashtable environment) throws NamingException {
		try {
			String classpathScannerClass = (String) environment.get("guice.classpath.scanner");
			if (classpathScannerClass == null || classpathScannerClass.length() == 0) {
				classpathScannerClass = "de.devsurf.injection.guice.scanner.asm.ASMClasspathScanner";
			}
			Class<ClasspathScanner> scannerClass = (Class<ClasspathScanner>) Class
				.forName(classpathScannerClass.trim());

			String classpathPackages = (String) environment.get("guice.classpath.packages");
			if (classpathPackages == null || classpathPackages.length() == 0) {
				classpathPackages = "com;de;org;net";
			}
			List<PackageFilter> packages = new ArrayList<PackageFilter>();

			StringTokenizer tok = new StringTokenizer(classpathPackages.trim(), ";");
			while (tok.hasMoreElements()) {
				packages.add(PackageFilter.create(tok.nextToken().trim()));
			}

			StartupModule startupModule = StartupModule.create(scannerClass, packages
				.toArray(new PackageFilter[packages.size()]));

			Injector injector = Injectors.createInjector(environment, startupModule,
				new AbstractModule() {
					protected void configure() {
						bind(Context.class).toProvider(new Provider<Context>() {
							@Inject
							Injector injector;

							public Context get() {
								JndiContext context = new JndiContext(environment);
								Properties jndiNames = createJndiNamesProperties(environment);
								try {
									JndiBindings.bindInjectorAndBindings(context, injector,
										jndiNames);
									return context;
								} catch (NamingException e) {
									throw new ProvisionException(
										"Failed to create JNDI bindings. Reason: " + e, e);
								}
							}
						}).in(Scopes.SINGLETON);
					}
				});
			return injector.getInstance(Context.class);
		} catch (Exception e) {
			NamingException exception = new NamingException(e.getMessage());
			exception.initCause(e);
			throw exception;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Properties createJndiNamesProperties(Hashtable environment) {
		Set<Map.Entry> set = environment.entrySet();
		Properties answer = new Properties();
		for (Entry entry : set) {
			String key = entry.getKey().toString();
			if (key.startsWith(NAME_PREFIX)) {
				String name = key.substring(NAME_PREFIX.length());
				Object value = entry.getValue();
				answer.put(name, value);
			}
		}
		return answer;
	}
}
