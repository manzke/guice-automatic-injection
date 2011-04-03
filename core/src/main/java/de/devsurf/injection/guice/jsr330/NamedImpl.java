package de.devsurf.injection.guice.jsr330;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.inject.Named;

class NamedImpl implements Named, Serializable {

	private final String value;

	public NamedImpl(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public int hashCode() {
		// This is specified in java.lang.Annotation.
		return (127 * "value".hashCode()) ^ value.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Named)) {
			return false;
		}

		Named other = (Named) o;
		return value.equals(other.value());
	}

	public String toString() {
		return "@" + Named.class.getName() + "(value=" + value + ")";
	}

	public Class<? extends Annotation> annotationType() {
		return Named.class;
	}

	private static final long serialVersionUID = 0;
}
