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
package de.devsurf.injection.guice.annotations;

import java.lang.annotation.Annotation;

import javax.inject.Named;

import de.devsurf.injection.guice.annotations.To.Type;
import de.devsurf.injection.guice.jsr330.Names;

public class Annotations {
	public static Bind createBind(final Type type){
		return new Bind() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return Bind.class;
			}
			
			@Override
			public Named value() {
				return Names.named("");
			}
			
			@Override
			public To to() {
				return createTo(type);
			}
			
			@Override
			public boolean multiple() {
				return false;
			}
		};
	}
	
	public static To createTo(final Type type){
		return new To() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return To.class;
			}
			
			@Override
			public Type value() {
				return type;
			}
			
			@Override
			public Class<? extends Object>[] customs() {
				return new Class<?>[0];
			}
		};
	}
}
