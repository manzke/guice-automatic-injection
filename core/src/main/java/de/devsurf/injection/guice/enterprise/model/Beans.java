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
package de.devsurf.injection.guice.enterprise.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "beans", namespace = "http://java.sun.com/xml/ns/javaee")
public class Beans {
	@XmlElement(name="interceptors", namespace = "http://java.sun.com/xml/ns/javaee")
    protected Interceptors interceptors;
    @XmlElement(name="decorators", namespace = "http://java.sun.com/xml/ns/javaee")
    protected Decorators decorators;
    @XmlElement(name="alternatives", namespace = "http://java.sun.com/xml/ns/javaee")
    protected Alternatives alternatives;

    public Interceptors getInterceptors() {
    	if(interceptors == null){
    		interceptors = new Interceptors();
    	}
        return interceptors;
    }

    public void setInterceptors(Interceptors value) {
        this.interceptors = value;
    }

    public Decorators getDecorators() {
    	if(decorators == null){
    		decorators = new Decorators();
    	}
        return decorators;
    }

    public void setDecorators(Decorators value) {
        this.decorators = value;
    }

    
    public Alternatives getAlternatives() {
    	if(alternatives == null){
    		alternatives = new Alternatives();
    	}
        return alternatives;
    }

    public void setAlternatives(Alternatives value) {
        this.alternatives = value;
    }
}
