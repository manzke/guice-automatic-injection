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
package de.devsurf.injection.guice.sonatype;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.scanners.ClassSpaceVisitor;

import de.devsurf.injection.guice.scanner.AnnotationListener;

/**
 * Visitor implementation to collect field annotation information from class.
 */
public class AnnotationCollector implements ClassVisitor, ClassSpaceVisitor {
    private String _name;
    private Class<?> _class;
    private boolean _isAnnotation;
    private Map<String, Annotation> _annotations;

    private List<AnnotationListener> _listeners;

    public static final AnnotationVisitor EMPTY_ANNOTATION_VISITOR = new AnnotationVisitor() {
	@Override
	public void visitEnum(String name, String desc, String value) {
	}

	@Override
	public void visitEnd() {
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
	    return this;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
	    return this;
	}

	@Override
	public void visit(String name, Object value) {
	}
    };

    public AnnotationCollector() {
	_listeners = new LinkedList<AnnotationListener>();
	_annotations = new HashMap<String, Annotation>();
    }

    public void addListener(AnnotationListener listener) {
	_listeners.add(listener);
    }

    public void removerListener(AnnotationListener listener) {
	_listeners.remove(listener);
    }

    public List<AnnotationListener> getListeners() {
	return new ArrayList<AnnotationListener>(_listeners);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
	    String[] interfaces) {
	_name = name.replace('/', '.');
	for (String interf : interfaces) {
	    if (interf.equals("java/lang/annotation/Annotation")) {
		_isAnnotation = true;
		return;
	    }
	}
    }

    @SuppressWarnings("unchecked")
    public AnnotationVisitor visitAnnotation(String sig, boolean visible) {
	if (_isAnnotation) {
	    return EMPTY_ANNOTATION_VISITOR;
	}
	String annotationClassStr = sig.replace('/', '.').substring(1, sig.length() - 1);
	if (_class == null) {
	    try {
		_class = getClass().getClassLoader().loadClass(_name);
	    } catch (ClassNotFoundException e) {
		e.printStackTrace();
		return EMPTY_ANNOTATION_VISITOR;
	    }
	}
	try {
	    Class<Annotation> annotationClass = (Class<Annotation>) getClass().getClassLoader()
		.loadClass(annotationClassStr);
	    Annotation annotation = _class.getAnnotation(annotationClass);
	    _annotations.put(annotationClassStr, annotation);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}

	return EMPTY_ANNOTATION_VISITOR;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void visitEnd() {
	if (!_isAnnotation && _annotations.size() > 0) {
	    for (AnnotationListener listener : _listeners) {
		listener.found((Class<Object>) _class, Collections.unmodifiableMap(_annotations));
	    }
	}
	_name = null;
	_class = null;
	_isAnnotation = false;
	_annotations.clear();
    }

    @Override
    public void visitAttribute(Attribute attr) {
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature,
	    Object value) {
	return null;
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
	    String[] exceptions) {
	return null;
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
    }

    @Override
    public void visitSource(String source, String debug) {
    }

    @Override
    public void visit(ClassSpace space) {

    }

    @Override
    public ClassVisitor visitClass(URL url) {
	return this;
    }
}