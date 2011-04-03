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
package de.devsurf.injection.guice.serviceloader;

import java.util.Set;

import javax.inject.Inject;

import com.google.inject.Module;

import de.devsurf.injection.guice.scanner.features.ScannerFeature;

//@GuiceModule(stage=BindingStage.INTERNAL)
public class ServiceLoaderModule extends ModuleLoader<Module> {
	private boolean enabled;

	public ServiceLoaderModule(Class<Module> type) {
		super(type);
	}
	
	@Inject
	public void init(Set<ScannerFeature> features){
		for(ScannerFeature feature : features){
			if(feature instanceof ServiceLoaderFeature){
				enabled = true;
			}
		}
	}
	
	@Override
	protected void configure() {
		if(enabled){
			super.configure();
		}
	}
}
