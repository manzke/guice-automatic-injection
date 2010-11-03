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
package de.devsurf.injection.guice.integrations.guicyfruit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.guiceyfruit.jsr250.Jsr250Module;

import de.devsurf.injection.guice.annotations.GuiceModule;
import de.devsurf.injection.guice.install.InstallationContext.BindingStage;

/**
 * JSR250-Module provided by GuicyFruit, so {@link PostConstruct},
 * {@link Resource} and {@link PreDestroy} can be used.
 * 
 * @author Daniel Manzke
 * 
 */
@GuiceModule(stage = BindingStage.BOOT)
public class JSR250Module extends Jsr250Module {
	public JSR250Module() {
		super();
	}
}
