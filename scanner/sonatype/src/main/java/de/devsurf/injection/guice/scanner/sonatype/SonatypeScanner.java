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
package de.devsurf.injection.guice.scanner.sonatype;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.reflect.URLClassSpace;
import org.sonatype.guice.bean.scanners.ClassSpaceScanner;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.features.ScannerFeature;

/**
 * {@link ClasspathScanner} Implementation which uses the Google Guice-Extension
 * which is provided by Sonatype. This Implementation scans all provided
 * packages.
 * 
 * @author Daniel Manzke
 * 
 */
public class SonatypeScanner implements ClasspathScanner {
	private Logger _logger = Logger.getLogger(SonatypeScanner.class.getName());
	private FilterAnnotationCollector _collector;
	private List<Pattern> _packagePatterns;

	@Inject
	@Named("classpath")
	private URL[] classPath;
	
	@Inject
	public SonatypeScanner(Set<ScannerFeature> features, @Named("packages") PackageFilter... filter) {
		_packagePatterns = new ArrayList<Pattern>();
		_collector = new FilterAnnotationCollector() {
			@Override
			public boolean matches(String name) {
				for (Pattern pattern : _packagePatterns) {
					if (pattern.matcher(name).matches()) {
						return true;
					}
				}
				return false;
			}
		};

		for (PackageFilter p : filter) {
			includePackage(p);
		}

		for (ScannerFeature feature : features) {
			addFeature(feature);
		}
	}
	
	@Override
	public void destroy() {
		_packagePatterns.clear();
		_packagePatterns = null;
		_collector.destroy();
		_collector = null;
	}

	@Override
	public void addFeature(ScannerFeature listener) {
		_collector.addScannerFeature(listener);
	}

	@Override
	public void removeFeature(ScannerFeature listener) {
		_collector.removerScannerFeature(listener);
	}

	@Override
	public List<ScannerFeature> getFeatures() {
		return _collector.getScannerFeatures();
	}

	@Override
	public void excludePackage(PackageFilter filter) {
	}
	
	@Override
	public void includePackage(final PackageFilter filter) {
		String packageName = filter.getPackage();
		String pattern = ".*" + packageName.replace(".", "\\.");

		if (filter.deep()) {
			pattern = pattern + "\\.((?:\\w|\\.)+([A-Z](?:\\w|\\$)+))";
		} else {
			pattern = pattern + "\\.([A-Z](?:\\w|\\$)+)";
		}

		if (_logger.isLoggable(Level.FINE)) {
			_logger.fine("Including Package for scanning: " + packageName + " generating Pattern: "
					+ pattern);
		}
		_packagePatterns.add(Pattern.compile(pattern));
	}

	@Override
	public void scan() throws IOException {
		ClassSpace space = new URLClassSpace(getClass().getClassLoader(), classPath);
		ClassSpaceScanner scanner = new ClassSpaceScanner(space);
		scanner.accept(_collector);
	}
}
