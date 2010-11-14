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
package de.devsurf.injection.guice.integrations.metro;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.inject.Module;
import com.sun.xml.ws.transport.http.servlet.WSServletContextListener;

public abstract class GuiceContextListener implements ServletContextListener{
	private WSServletContextListener delegate = new WSServletContextListener();

	public void attributeAdded(ServletContextAttributeEvent event) {
		delegate.attributeAdded(event);
	}

	public void attributeRemoved(ServletContextAttributeEvent event) {
		delegate.attributeRemoved(event);
	}

	public void attributeReplaced(ServletContextAttributeEvent event) {
		delegate.attributeReplaced(event);
	}

	public void contextDestroyed(ServletContextEvent event) {
		delegate.contextDestroyed(event);
	}

	public void contextInitialized(ServletContextEvent event) {
		AutomaticGuiceManager.inject(getModule());
		delegate.contextInitialized(event);
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public int hashCode() {
		return delegate.hashCode();
	}

	public String toString() {
		return delegate.toString();
	}
	
	protected abstract Module getModule();
}
