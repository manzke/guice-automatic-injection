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

import javax.inject.Provider;

@SuppressWarnings("rawtypes")
public class BindingJob {
	public final Class<? extends Annotation> scoped;
	public final Class<? extends Provider> provided;
	public final Annotation annotated;
	public final String className;
	public final String interfaceName;

	public BindingJob(Class<? extends Annotation> scoped, Class<? extends Provider> provided, Annotation annotated, String className,
			String interfaceName) {
		this.scoped = scoped;
		this.provided = provided;
		this.annotated = annotated;
		this.className = className;
		this.interfaceName = interfaceName;
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BindingJob other = (BindingJob) obj;
		if (annotated == null) {
			if (other.annotated != null)
				return false;
		} else if (!annotated.equals(other.annotated))
			return false;
		if (interfaceName == null) {
			if (className == null) {
				if (other.className != null)
					return false;
			} else if (!className.equals(other.className))
				return false;
		}
		if (interfaceName == null) {
			if (other.interfaceName != null)
				return false;
		} else if (!interfaceName.equals(other.interfaceName))
			return false;
		if (provided == null) {
			if (other.provided != null)
				return false;
		} else if (!provided.equals(other.provided))
			return false;
		if (scoped == null) {
			if (other.scoped != null)
				return false;
		} else if (!scoped.equals(other.scoped))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BindingJob [annotated=" + annotated + ", className=" + className
				+ ", interfaceName=" + interfaceName + ", provided=" + provided + ", scoped="
				+ scoped + "]";
	}
}
