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
package de.devsurf.injection.guice.install.bindjob;

import java.lang.annotation.Annotation;



public class MultiBindingJob extends BindingJob{

	public MultiBindingJob(Class<? extends Annotation> scoped, Annotation annotated,
			String className, String interfaceName) {
		super(scoped, null, annotated, className, interfaceName);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotated == null) ? 0 : annotated.hashCode());
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((interfaceName == null) ? 0 : interfaceName.hashCode());
		result = prime * result + ((provided == null) ? 0 : provided.hashCode());
		result = prime * result + ((scoped == null) ? 0 : scoped.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Multi"+super.toString();
	}
}
