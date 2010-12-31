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
package de.devsurf.injection.guice.scanner.asm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.PackageFilter;
import de.devsurf.injection.guice.scanner.features.ScannerFeature;

/**
 * This Implementation only uses the ASM-API to read all recognized classes. It
 * doesn't depend on any further 3rd-Party libraries.
 * 
 * @author Daniel Manzke
 * 
 */
public class ASMClasspathScanner implements ClasspathScanner {
	public static String LINE_SEPARATOR = System.getProperty("line.separator");
	private Logger _logger = Logger.getLogger(ASMClasspathScanner.class.getName());

	@Inject
	@Named("classpath")
	private URL[] classPath;
	private List<Pattern> patterns = new ArrayList<Pattern>();
	private int count;
	private Set<String> visited;
	private BlockingQueue<AnnotationCollector> collectors;
	
	@Inject
	public ASMClasspathScanner(Set<ScannerFeature> listeners,
			@Named("packages") PackageFilter... filter) {
		int cores = Runtime.getRuntime().availableProcessors();
		this.collectors = new ArrayBlockingQueue<AnnotationCollector>(cores);

		for(int i=0;i<cores;i++){
			try {
				collectors.put(new AnnotationCollector());
			} catch (InterruptedException e) {
				// ignore
			}
		}
		for (PackageFilter p : filter) {
			includePackage(p);
		}

		for (ScannerFeature listener : listeners) {
			addFeature(listener);
		}
		visited = new HashSet<String>();
	}

	@Override
	public void addFeature(ScannerFeature feature) {
		for(AnnotationCollector collector : collectors){
			collector.addScannerFeature(feature);
		}
	}

	@Override
	public void removeFeature(ScannerFeature feature) {
		for(AnnotationCollector collector : collectors){
			collector.addScannerFeature(feature);
		}
	}

	@Override
	public List<ScannerFeature> getFeatures() {
		List<ScannerFeature> features;
		try {
			AnnotationCollector collector = collectors.take();
			features = collector.getScannerFeatures();
			collectors.put(collector);
		} catch (InterruptedException e) {
			// ignore
			features = Collections.emptyList();
		}
		return features;
	}

	@Override
	public void includePackage(final PackageFilter filter) {
		String packageName = filter.getPackage();
		String pattern = ".*" + packageName.replace(".", "/");

		if (filter.deep()) {
			pattern = pattern + "/(?:\\w|/)*([A-Z](?:\\w|\\$)+)\\.class$";
		} else {
			pattern = pattern + "/([A-Z](?:\\w|\\$)+)\\.class$";
		}

		if (_logger.isLoggable(Level.FINE)) {
			_logger.fine("Including Package for scanning: " + packageName + " generating Pattern: "
					+ pattern);
		}
		patterns.add(Pattern.compile(pattern));
	}

	@Override
	public void excludePackage(final PackageFilter filter) {
		// TODO Could use Predicate of Google
	}

	public void scan() throws IOException {
		ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());//Executors.newFixedThreadPool(1);
		if (_logger.isLoggable(Level.INFO)) {
			StringBuilder builder = new StringBuilder();
			builder.append("Using Root-Path for Classpath scanning:").append(LINE_SEPARATOR);
			for (URL url : classPath) {
				builder.append(url.toString()).append(LINE_SEPARATOR);
			}
			_logger.log(Level.INFO, builder.toString());
		}
		List<Future<?>> futures = new ArrayList<Future<?>>();
		for (final URL url : classPath) {
			Future<?> task = pool.submit(new Runnable() {
				@Override
				public void run() {
					try {
						if (url.toString().startsWith("jar:")) {
							visitJar(url);
							return;
						}
						URI uri;
						File entry;
						try {
							uri = url.toURI();
							entry = new File(uri);
							if (!entry.exists()) {
								_logger.log(Level.FINE, "Skipping Entry " + entry
										+ ", because it doesn't exists.");
								return;
							}
						} catch (URISyntaxException e) {
							// ignore
							_logger.log(Level.WARNING, "Using invalid URL for Classpath Scanning: "
									+ url, e);
							return;
						}

						if (entry.isDirectory()) {
							visitFolder(entry);
						} else {
							String path = uri.toString();
							if (matches(path)) {
								if (!visited.contains(entry.getAbsolutePath())) {
									visitClass(new FileInputStream(entry));
									visited.add(entry.getAbsolutePath());
								}
							} else if (path.endsWith(".jar")) {
								visitJar(entry);
							}
						}
					} catch (FileNotFoundException e) {
						_logger.log(Level.FINE, "Skipping Entry " + url
							+ ", because it doesn't exists.",e);
					} catch (IOException e) {
						_logger.log(Level.FINE, "Skipping Entry " + url
							+ ", because it couldn't be scanned.",e);
					}
				}
			});
			futures.add(task);
		}
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        pool.shutdown();
        destroy();
	}
	
	public void destroy(){
		classPath = null;
		collectors.clear();
		collectors = null;
		patterns.clear();
		patterns = null;
		visited.clear();
		visited = null;
	}

	private void visitFolder(File folder) throws IOException {
		_logger.log(Level.FINE, "Scanning Folder: " + folder.getAbsolutePath());
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				visitFolder(file);
			} else {
				String path = file.toURI().toString();
				if (matches(path)) {
					if (!visited.contains(file.getAbsolutePath())) {
						visitClass(new FileInputStream(file));
						visited.add(file.getAbsolutePath());
					}
				} else if (path.endsWith(".jar")) {
					visitJar(file);
				}
			}
		}
	}

	private void visitJar(URL url) throws IOException {
		if (_logger.isLoggable(Level.FINE)) {
			_logger.log(Level.FINE, "Scanning JAR-File: " + url);
		}

		JarURLConnection conn = (JarURLConnection) url.openConnection();
		_visitJar(conn.getJarFile());
	}

	private void visitJar(File file) throws IOException {
		if (_logger.isLoggable(Level.FINE)) {
			_logger.log(Level.FINE, "Scanning JAR-File: " + file.getAbsolutePath());
		}
		JarFile jarFile = new JarFile(file);
		_visitJar(jarFile);
	}

	private void _visitJar(JarFile jarFile) throws IOException {
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		for (JarEntry jarEntry = null; jarEntries.hasMoreElements();) {
			count++;
			jarEntry = jarEntries.nextElement();
			String name = jarEntry.getName();

			if (!jarEntry.isDirectory() && matches(name)) {
				if (!visited.contains(name)) {
					visitClass(jarFile.getInputStream(jarEntry));
					visited.add(name);
				}
			}
		}
	}

	private void visitClass(InputStream in) throws IOException {
		count++;
		ClassReader reader = new ClassReader(new BufferedInputStream(in));
		try {
			AnnotationCollector collector = collectors.take();
			reader.accept(collector, AnnotationCollector.ASM_FLAGS);
			collectors.put(collector);
		} catch (InterruptedException e) {
			// ignore
		}
	}

	private boolean matches(String name) {
		for (Pattern pattern : patterns) {
			if (pattern.matcher(name).matches()) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		String p = "((?:\\w|/)+)/([a-zA-Z_\\$][\\w\\$]*)*\\.class$";

		List<String> ps = Arrays.asList("com/saperion/test/Impl$Test.class",
			"com/saperion/test/Impl.class", "C:/programme/com/saperion/test/Impl$Test.class",
			"C:/programme/com/saperion/test/Impl.class",
			"C:/programme/myjar.jar!com/saperion/test/Impl$Test.class",
			"C:/programme/myjar.jar!com/saperion/test/Impl.class");

		Pattern pattern = Pattern.compile(p, Pattern.COMMENTS);
		for (String s : ps) {
			Matcher matcher = pattern.matcher(s);
			if (matcher.matches()) {
				System.out.println("Num groups: " + matcher.groupCount());
				System.out.println("Package: " + matcher.group(1));
				System.out.println("Class: " + matcher.group(2));
			} else {
				System.err.println("Input does not match pattern.");
			}
		}
		
		String file = "F:\\git\\twiddns\\target\\classes\\de\\devsurf\\twiddns\\test\\Publisher.class".replace("\\", "/");
		String packageName = "de.devsurf.twiddns";						
		String patternStr = ".*" + packageName.replace(".", "/");

		patternStr = patternStr + "/(?:\\w|/)*([A-Z](?:\\w|\\$)+)\\.class$";
		pattern = Pattern.compile(patternStr);
		
		Matcher matcher = pattern.matcher(file);
		System.out.println(matcher.matches());
		System.out.println(":D");
	}
}
