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
package de.devsurf.injection.guice.scanner;

import java.io.IOException;
import java.util.List;

import de.devsurf.injection.guice.scanner.features.ScannerFeature;

/**
 * Interface which is used to create ClasspathScanner implementations. Our
 * StartupModule will bind your chosen Implementation to this interface. You
 * choose which ClasspathScanner should be used, by passing the Class to the
 * StartupModule constructor.
 * 
 * @author Daniel Manzke
 * 
 */
public interface ClasspathScanner {
	/**
	 * Starts the Classpath Scanning and the Registration of Requests for Bindings. Called through the StartupModule.
	 * 
	 * @throws IOException
	 */
	void scan() throws IOException;

	/**
	 * Adds a ScannerFeature to the Scanner like Automatic Binding of Classes or Guice Modules, Interceptors, ... or your own one.
	 * @param feature
	 */
	void addFeature(ScannerFeature feature);

	void removeFeature(ScannerFeature feature);

	List<ScannerFeature> getFeatures();

	/**
	 * Adds a Package which should be included to scan. Only Classes found in the included Packages will be read and passed to the ScannerFeatures.
	 * @param filter
	 */
	void includePackage(PackageFilter filter);

	void excludePackage(PackageFilter filter);
	
	/**
	 * Destroys a ClasspathScanner so it can do some kind of Cleanup.
	 */
	void destroy();
}
