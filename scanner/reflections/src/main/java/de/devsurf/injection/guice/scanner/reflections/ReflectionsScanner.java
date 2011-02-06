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
package de.devsurf.injection.guice.scanner.reflections;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.annotation.Annotation;

import org.reflections.Reflections;
import org.reflections.scanners.AbstractScanner;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.features.ScannerFeature;

/**
 * {@link ClasspathScanner} Implementation which uses the Reflections-API. This
 * Implementation scans all passed packages multithreaded.
 * 
 * @author Daniel Manzke
 * 
 */
public class ReflectionsScanner implements ClasspathScanner {
	private Logger _logger = Logger.getLogger(ReflectionsScanner.class.getName());
	private List<ScannerFeature> features;
	private List<Pattern> packagePatterns;

	@Inject
	@Named("classpath")
	private URL[] classPath;

	@Inject
	public ReflectionsScanner(Set<ScannerFeature> scannerFeatures, @Named("packages") PackageFilter... filter) {
		features = new ArrayList<ScannerFeature>(scannerFeatures);
		this.packagePatterns = new ArrayList<Pattern>();
		for (PackageFilter p : filter) {
			includePackage(p);
		}
	}
	
	@Override
	public void destroy() {
		features.clear();
		features = null;
		packagePatterns.clear();
		packagePatterns = null;
		classPath = null;
	}

	@Override
	public void addFeature(ScannerFeature listener) {
		features.add(listener);
	}

	@Override
	public void removeFeature(ScannerFeature listener) {
		features.remove(listener);
	}

	@Override
	public List<ScannerFeature> getFeatures() {
		return new ArrayList<ScannerFeature>(features);
	}

	@Override
	public void excludePackage(final PackageFilter filter) {
	}
	
	@Override
	public void includePackage(final PackageFilter filter) {
		String packageName = filter.getPackage();
		String pattern = packageName.replace(".", "\\.");

		if (filter.deep()) {
			pattern = pattern + "\\.(?:\\w|\\.)*([A-Z](?:\\w|\\$)+)\\.class$";
		} else {
			pattern = pattern + "\\.([A-Z](?:\\w|\\$)+)\\.class$";
		}

		if (_logger.isLoggable(Level.FINE)) {
			_logger.fine("Including Package for scanning: " + packageName + " generating Pattern: "
					+ pattern);
		}
		packagePatterns.add(Pattern.compile(pattern));
	}

	@Override
	public void scan() throws IOException {
		new Reflections(new ConfigurationBuilder().setScanners(new AnnotationScanner())
			.filterInputsBy(new Predicate<String>() {
				@Override
				public boolean apply(String input) {
					return matches(input);
				}
			}).setUrls(classPath).useParallelExecutor());
	}

	private boolean matches(String name) {
		for (Pattern pattern : packagePatterns) {
			if (pattern.matcher(name).matches()) {
				return true;
			}
		}
		return false;
	}

	private class AnnotationScanner extends AbstractScanner {
		@SuppressWarnings("unchecked")
		@Override
		public void scan(final Object cls) {
			ClassFile classFile = (ClassFile) cls;
			AnnotationsAttribute annotationsAttribute = (AnnotationsAttribute) classFile
				.getAttribute(AnnotationsAttribute.visibleTag);
			if (annotationsAttribute == null) {
				return;
			}

			Class<Object> objectClass;
			try {
				objectClass = (Class<Object>) Class.forName(classFile.getName());
			} catch (ClassNotFoundException e) {
				ReflectionsScanner.this._logger.log(Level.WARNING,
					"Failure while trying to load the Class \"" + classFile.getName()
							+ "\" from Classpath.", e);
				return;
			}

			Map<String, java.lang.annotation.Annotation> map = new HashMap<String, java.lang.annotation.Annotation>();
			for (Annotation annotation : annotationsAttribute.getAnnotations()) {
				Class<java.lang.annotation.Annotation> annotationClass;
				try {
					annotationClass = (Class<java.lang.annotation.Annotation>) Class
						.forName(annotation.getTypeName());
					map.put(annotationClass.getName(), objectClass.getAnnotation(annotationClass));
				} catch (ClassNotFoundException e) {
					ReflectionsScanner.this._logger.log(Level.WARNING,
						"Failure while trying to load the Annotations from Classpath.", e);
					continue;
				}
			}
			
			if(map.containsKey("javax.enterprise.inject.Alternative")){
				return;
			}

			for (ScannerFeature feature : features) {
				feature.found(objectClass, map);
			}
		}
	}
}
