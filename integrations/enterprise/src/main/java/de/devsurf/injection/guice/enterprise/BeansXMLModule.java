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
package de.devsurf.injection.guice.enterprise;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.google.inject.Binder;
import com.google.inject.Module;

import de.devsurf.injection.guice.annotations.GuiceModule;
import de.devsurf.injection.guice.enterprise.model.Alternatives;
import de.devsurf.injection.guice.enterprise.model.Beans;
import de.devsurf.injection.guice.enterprise.model.Decorators;
import de.devsurf.injection.guice.enterprise.model.Interceptors;
import de.devsurf.injection.guice.install.BindingStage;
import de.devsurf.injection.guice.scanner.features.BindingScannerFeature;
import de.devsurf.injection.guice.scanner.features.ScannerFeature;

@GuiceModule(stage=BindingStage.INTERNAL)
public class BeansXMLModule implements Module {
	private Logger _logger = Logger.getLogger(BeansXMLModule.class.getName());

	private boolean enabled;
	private Set<ScannerFeature> features;
	
	@Inject
	public void init(Set<ScannerFeature> features){
		for(ScannerFeature feature : features){
			if(feature instanceof BeansXMLFeature){
				enabled = true;
			}
		}
		this.features = features;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void configure(Binder binder) {		
		if(enabled){
			//FIXME
			URL beansURL = getClass().getResource("/META-INF/beans.xml");
//			URL beansURL = getClass().getResource("/beans.xml");
			if(beansURL != null){
				Beans beans;
				try {
					JAXBContext context = JAXBContext.newInstance(Alternatives.class, Beans.class, Decorators.class, Interceptors.class);
					beans = (Beans) context.createUnmarshaller().unmarshal(beansURL);
				} catch (JAXBException e) {
					_logger.log(Level.WARNING, "Failure occured while beans.xml should be unmarshalled.", e);
					return;
				}

				Alternatives alternatives = beans.getAlternatives();
				List<String> classes = alternatives.getClasses();
				for(String className : classes){
					try {
						Class<Object> clazz = (Class<Object>) Class.forName(className);
						Annotation[] annotations = clazz.getAnnotations();
						Map<String, Annotation> map = new HashMap<String, Annotation>();
						for(Annotation annotation : annotations){
							map.put(annotation.annotationType().getName(), annotation);
						}
						
						scan(clazz, map);
					} catch (Exception e) {
						_logger.log(Level.WARNING, "Class \""+className+"\" could not be found.", e);
						continue;
					}
				}
				
				Interceptors interceptors = beans.getInterceptors();
				classes = interceptors.getClasses();
				for(String className : classes){
					try {
						Class<Object> clazz = (Class<Object>) Class.forName(className);
						Annotation[] annotations = clazz.getAnnotations();
						Map<String, Annotation> map = new HashMap<String, Annotation>();
						for(Annotation annotation : annotations){
							map.put(annotation.annotationType().getName(), annotation);
						}
						map.put(Interceptor.class.getName(), Annotations.createInterceptor());
						
						scan(clazz, map);
					} catch (Exception e) {
						_logger.log(Level.WARNING, "Class \""+className+"\" could not be found.", e);
						continue;
					}
				}
			}else{
				_logger.log(Level.INFO, "Beans.xml Feature was bound, but not found in Classpath.");
			}
		}
	}

	private void scan(Class<Object> clazz, Map<String, Annotation> map) {
		for (ScannerFeature feature : features) {
			if(feature instanceof BindingScannerFeature){
				BindingScannerFeature f = (BindingScannerFeature)feature;
				if(f.accept(clazz, map) != BindingStage.IGNORE){
					f.process(clazz, map);
				}
			}else{
				feature.found(clazz, map);	
			}
		}
	}
}
