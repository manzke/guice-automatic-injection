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

import java.net.URL;

import org.objectweb.asm.ClassVisitor;
import org.sonatype.guice.bean.reflect.ClassSpace;
import org.sonatype.guice.bean.scanners.ClassSpaceVisitor;

import de.devsurf.injection.guice.scanner.asm.AnnotationCollector;

/**
 * Visitor implementation to collect field annotation information from class.
 * 
 * @author Daniel Manzke
 */
public abstract class FilterAnnotationCollector extends AnnotationCollector implements
	ClassSpaceVisitor {
    public FilterAnnotationCollector() {
	super();
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
	    String[] interfaces) {
	_name = name.replace('/', '.');
	if (!matches(_name)) {
	    _isAnnotation = true;
	    return;
	}
	for (String interf : interfaces) {
	    if (interf.equals("java/lang/annotation/Annotation")) {
		_isAnnotation = true;
		return;
	    }
	}
    }

    public abstract boolean matches(String name);

    @Override
    public void visit(ClassSpace space) {
    }

    @Override
    public ClassVisitor visitClass(URL url) {
	return this;
    }
}