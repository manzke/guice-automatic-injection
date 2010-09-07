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
package de.devsurf.injection.guice.asm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassReader;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.scanner.AnnotationListener;
import de.devsurf.injection.guice.scanner.ClasspathScanner;

/**
 * This Implementation only uses the ASM-API to read all recognized classes. It
 * doesn't depend on any further 3rd-Party libraries.
 * 
 * @author Daniel Manzke
 * 
 */
public class VirtualClasspathReader implements ClasspathScanner {
    private File[] classPath;
    private LinkedList<Pattern> packagePatterns;
    private int count;
    private AnnotationCollector collector;

    @Inject
    public VirtualClasspathReader(Set<AnnotationListener> listeners,
	    @Named("packages") String... packages) {
	this.collector = new AnnotationCollector();
	this.packagePatterns = new LinkedList<Pattern>();
	for (String p : packages) {
	    includePackage(p);
	}

	for (AnnotationListener listener : listeners) {
	    addAnnotationListener(listener);
	}
    }

    @Override
    public void addAnnotationListener(AnnotationListener listener) {
	collector.addListener(listener);
    }

    @Override
    public void removeAnnotationListener(AnnotationListener listener) {
	collector.removerListener(listener);
    }

    @Override
    public List<AnnotationListener> getAnnotationListeners() {
	return collector.getListeners();
    }

    @Override
    public void includePackage(final String packageName) {
	String pattern = ".*" + packageName.replace(".", "\\\\") + ".*";
	packagePatterns.add(Pattern.compile(pattern));
    }

    @Override
    public void excludePackage(final String packageName) {
	// TODO
    }

    public void scan() throws IOException {
	this.classPath = findClassPaths();
	for (File entry : classPath) {
	    if (entry.isDirectory()) {
		visitFolder(entry);
	    } else {
		if (entry.getName().endsWith(".class")) {
		    visitClass(new FileInputStream(entry));
		} else if (entry.getName().endsWith(".jar")) {
		    visitJar(entry);
		}
	    }
	}
	System.out.println("Scanned " + count + " files.");
    }

    private void visitFolder(File folder) throws IOException {
	boolean matches = matches(folder.getAbsolutePath());
	File[] files = folder.listFiles();
	for (File file : files) {
	    if (file.isDirectory()) {
		visitFolder(file);
	    } else {
		if (file.getName().endsWith(".class") && matches) {
		    visitClass(new FileInputStream(file));
		} else if (file.getName().endsWith(".jar")) {
		    visitJar(file);
		}
	    }
	}
    }

    private void visitJar(File file) throws IOException {
	JarFile jarFile = new JarFile(file);
	Enumeration<JarEntry> jarEntries = jarFile.entries();
	for (JarEntry jarEntry = null; jarEntries.hasMoreElements();) {
	    count++;
	    jarEntry = jarEntries.nextElement();
	    String name = jarEntry.getName();
	    if (!jarEntry.isDirectory() && name.endsWith(".class") && matches(name)) {
		visitClass(jarFile.getInputStream(jarEntry));
	    }
	}
    }

    private void visitClass(InputStream in) throws IOException {
	count++;
	ClassReader reader = new ClassReader(new BufferedInputStream(in));
	reader.accept(collector, 1);
    }

    private boolean matches(String name) {
	for (Pattern pattern : packagePatterns) {
	    if (pattern.matcher(name).matches()) {
		return true;
	    }
	}
	return false;
    }

    public static File[] findClassPaths() {
	List<File> list = new ArrayList<File>();
	String classpath = System.getProperty("java.class.path");
	StringTokenizer tokenizer = new StringTokenizer(classpath, File.pathSeparator);

	while (tokenizer.hasMoreTokens()) {
	    String path = tokenizer.nextToken();
	    File fp = new File(path);
	    if (!fp.exists())
		throw new RuntimeException("File in java.class.path does not exist: " + fp);
	    list.add(fp);
	}
	return list.toArray(new File[list.size()]);
    }
}
