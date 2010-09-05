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

import java.io.IOException;


/**
 * Interface which is used to create ClasspathScanner implementations.
 * Our StartupModule will bind your chosen Implementation to this interface.
 * You choose which ClasspathScanner should be used, by passing the Class
 * to the StartupModule constructor.
 * 
 * @author Daniel Manzke
 *
 */
public interface ClasspathScanner {
	void scan() throws IOException;
	void addAnnotationListener(AnnotationListener listener);
	void removeAnnotationListener(AnnotationListener listener);
	void includePackage(String packageName);
	void excludePackage(String packageName);
}
