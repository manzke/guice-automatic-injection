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
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.scanner.ClasspathScanner;
import de.devsurf.injection.guice.scanner.ScannerFeature;

/**
 * This Implementation only uses the ASM-API to read all recognized classes. It
 * doesn't depend on any further 3rd-Party libraries.
 * 
 * @author Daniel Manzke
 * 
 */
public class ASMClasspathScanner implements ClasspathScanner {
    private static final int ASM_FLAGS = ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
    private Logger _logger = Logger.getLogger(ASMClasspathScanner.class.getName());
    
    @Inject
    @Named("classpath")
    private URL[] classPath;
    private List<Pattern> jarPatterns = new ArrayList<Pattern>();
    private List<Pattern> filePatterns = new ArrayList<Pattern>();
    private int count;
    private AnnotationCollector collector;
    private Set<String> visited;

    @Inject
    public ASMClasspathScanner(Set<ScannerFeature> listeners,
	    @Named("packages") String... packages) {
	this.collector = new AnnotationCollector();
	for (String p : packages) {
	    includePackage(p);
	}

	for (ScannerFeature listener : listeners) {
	    addScannerFeature(listener);
	}
	visited = new HashSet<String>();
    }

    @Override
    public void addScannerFeature(ScannerFeature listener) {
	collector.addScannerFeature(listener);
    }

    @Override
    public void removeScannerFeature(ScannerFeature listener) {
	collector.removerScannerFeature(listener);
    }

    @Override
    public List<ScannerFeature> getScannerFeatures() {
	return collector.getScannerFeatures();
    }

    @Override
    public void includePackage(final String packageName) {
	String jarPattern = ".*" + packageName.replace(".", "/") + ".*";
	String filePattern = ".*" + packageName.replace(".", "\\\\") + ".*";
	if(_logger.isLoggable(Level.FINE)){
	    _logger.fine("Including Package for scanning: "+packageName+" generating Pattern: "+filePattern);
	}
	filePatterns.add(Pattern.compile(filePattern));
	jarPatterns.add(Pattern.compile(jarPattern));
    }

    @Override
    public void excludePackage(final String packageName) {
	// TODO
    }

    public void scan() throws IOException {
	for (URL url : classPath) {
	    if(url.toString().startsWith("jar:")){
		visitJar(url);
		continue;
	    }
	    File entry;
	    try {
		entry = new File(url.toURI());
		if(!entry.exists()){
		    _logger.log(Level.FINE, "Skipping Entry "+entry+", because it doesn't exists.");
		    continue;
		}
	    } catch (URISyntaxException e) {
		//ignore
		_logger.log(Level.WARNING, "Using invalid URL for Classpath Scanning: "+url, e);
		continue;
	    }
	    _logger.info("Using Root-Path "+entry.getAbsolutePath()+" for Classpath scanning.");
	    if (entry.isDirectory()) {
		visitFolder(entry);
	    } else {
		if (entry.getName().endsWith(".class") && matches(entry.getAbsolutePath(), filePatterns)) {
		    if(!visited.contains(entry.getAbsolutePath())){
			visitClass(new FileInputStream(entry));
			visited.add(entry.getAbsolutePath());
		    }
		} else if (entry.getName().endsWith(".jar")) {
		    visitJar(entry);
		}
	    }
	}
    }

    private void visitFolder(File folder) throws IOException {
	_logger.log(Level.FINE, "Scanning Folder: "+folder.getAbsolutePath());
	boolean matches = matches(folder.getAbsolutePath(), filePatterns);
	File[] files = folder.listFiles();
	for (File file : files) {
	    if (file.isDirectory()) {
		visitFolder(file);
	    } else {
		if (file.getName().endsWith(".class") && matches) {
		    if(!visited.contains(file.getAbsolutePath())){
			visitClass(new FileInputStream(file));
			visited.add(file.getAbsolutePath());
		    }
		} else if (file.getName().endsWith(".jar")) {
		    visitJar(file);
		}
	    }
	}
    }
    
    private void visitJar(URL url) throws IOException {
	_logger.log(Level.FINE, "Scanning JAR-File: "+url);
	JarURLConnection conn = (JarURLConnection) url.openConnection();
	
	_visitJar(conn.getJarFile());
    }
    
    private void visitJar(File file) throws IOException {
	_logger.log(Level.FINE, "Scanning JAR-File: "+file.getAbsolutePath());
	JarFile jarFile = new JarFile(file);
	_visitJar(jarFile);
    }

    private void _visitJar(JarFile jarFile) throws IOException {
	Enumeration<JarEntry> jarEntries = jarFile.entries();
	for (JarEntry jarEntry = null; jarEntries.hasMoreElements();) {
	    count++;
	    jarEntry = jarEntries.nextElement();
	    String name = jarEntry.getName();

	    if (!jarEntry.isDirectory() && name.endsWith(".class") && matches(name, jarPatterns)) {
		if(!visited.contains(name)){		    
		    visitClass(jarFile.getInputStream(jarEntry));
		    visited.add(name);
		}
	    }
	}
    }

    private void visitClass(InputStream in) throws IOException {
	count++;
	ClassReader reader = new ClassReader(new BufferedInputStream(in));
	reader.accept(collector, ASM_FLAGS);
    }

    private boolean matches(String name, List<Pattern> patterns) {
	for (Pattern pattern : patterns) {
	    if (pattern.matcher(name).matches()) {
		return true;
	    }
	}
	return false;
    }
}
