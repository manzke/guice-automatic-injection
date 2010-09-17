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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;

import de.devsurf.injection.guice.scanner.InstallationContext.BindingStage;
import de.devsurf.injection.guice.scanner.InstallationContext.StageableRequest;

/**
 * Default Implementation for Annotation Listeners, which should stay informed
 * abbout found annotated classes. Due the fact, that we need the Binder of the
 * Child Injector, it will be set at runtime by the {@link ScannerModule}.
 * 
 * @author Daniel Manzke
 * 
 */
public abstract class GuiceAnnotationListener implements AnnotationListener {
    protected Binder _binder;
    @Inject
    protected Injector injector;
    @Inject
    protected InstallationContext context;

    public void setBinder(Binder binder) {
	_binder = binder;
    }
        
    @Override
    public void found(final Class<Object> annotatedClass, final Map<String, Annotation> annotations) {
	final BindingStage stage = accept(annotatedClass, annotations);
	if(stage != BindingStage.IGNORE){
	    context.add(new StageableRequest() {    
		private Class<Object> _annotatedClass = annotatedClass;
		private Map<String, Annotation> _annotations = new HashMap<String, Annotation>(annotations);
		
	        @Override
	        public Void call() throws Exception {           
	            process(_annotatedClass, _annotations);
	            return null;
	        }
	        
	        @Override
	        public BindingStage getExecutionStage() {
	            return stage;
	        }
	    });
	}
    }
    
    public abstract BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations);
    
    public abstract void process(Class<Object> annotatedClass, Map<String, Annotation> annotations);
}
