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
package de.devsurf.injection.guice.enterprise.features;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.google.inject.servlet.ServletModule;
import com.googlecode.rocoto.configuration.resolver.PropertiesResolver;

import de.devsurf.injection.guice.install.BindingStage;
import de.devsurf.injection.guice.scanner.features.BindingScannerFeature;

@Singleton
public class WebServletBindingFeature extends BindingScannerFeature {
	private static Logger LOGGER = Logger.getLogger(WebServletBindingFeature.class.getName());
	
	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		if (annotations.containsKey(WebServlet.class.getName())) {
			return BindingStage.BINDING;
		}
		return BindingStage.IGNORE;
	}

	@Override
	public void process(final Class<Object> annotatedClass,
			final Map<String, Annotation> annotations) {
		final WebServlet annotation = (WebServlet) annotations.get(WebServlet.class.getName());
		
		String[] patterns = annotation.value();
		final String value;
		final String[] values;
		if(patterns.length > 0){
			final PropertiesResolver resolver = new PropertiesResolver(patterns[0]);
			resolver.setInjector(injector);
			value = resolver.get();
			if(patterns.length > 1){
				values = new String[patterns.length-1];
				
				for(int i=1;i<patterns.length;i++){
					final PropertiesResolver patternResolver = new PropertiesResolver(patterns[i]);
					patternResolver.setInjector(injector);
					values[i-1] = patternResolver.get();
				}				
			}else{
				values = null;
			}
		}else{
			// failure
			LOGGER.info("Ignoring Servlet \""+annotatedClass+"\", because no Values for URLs were specified.");
			return;
		}

		_binder.install(new ServletModule(){
			@Override
			protected void configureServlets() {
				serve(value).with(annotatedClass.asSubclass(HttpServlet.class));
			}
		});
	}
}
