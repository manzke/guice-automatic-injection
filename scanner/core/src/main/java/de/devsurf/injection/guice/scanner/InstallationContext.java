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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class InstallationContext {
    private Map<BindingStage, List<Callable<?>>> context = new HashMap<BindingStage, List<Callable<?>>>();

    public void process() throws Exception {
	for (BindingStage stage : BindingStage.ORDERED) {
	    List<Callable<?>> requests = context.get(stage);
	    if (requests != null) {
		for (Callable<?> request : requests) {
		    request.call();
		}
	    }
	}
    }

    public void add(BindingStage stage, Callable<?> request) {
	synchronized (context) {
	    List<Callable<?>> requests = context.get(stage);
	    if (requests == null) {
		requests = new ArrayList<Callable<?>>();
		context.put(stage, requests);
	    }
	    requests.add(request);
	}
    }

    public void add(StageableRequest request) {
	synchronized (context) {
	    List<Callable<?>> requests = context.get(request.getExecutionStage());
	    if (requests == null) {
		requests = new ArrayList<Callable<?>>();
		context.put(request.getExecutionStage(), requests);
	    }
	    requests.add(request);
	}
    }

    public static interface StageableRequest extends java.util.concurrent.Callable<Void> {
	BindingStage getExecutionStage();
    }

    public static enum BindingStage {
	BOOT_BEFORE, BOOT, BOOT_POST, BINDING_BEFORE, BINDING, BINDING_POST, INSTALL_BEFORE, INSTALL, INSTALL_POST, BUILD_BEFORE, BUILD, BUILD_POST, IGNORE;

	public static final List<BindingStage> ORDERED = new LinkedList<BindingStage>();

	static {
	    ORDERED.add(BOOT_BEFORE);
	    ORDERED.add(BOOT);
	    ORDERED.add(BOOT_POST);
	    ORDERED.add(BINDING_BEFORE);
	    ORDERED.add(BINDING);
	    ORDERED.add(BINDING_POST);
	    ORDERED.add(INSTALL_BEFORE);
	    ORDERED.add(INSTALL);
	    ORDERED.add(INSTALL_POST);
	    ORDERED.add(BUILD_BEFORE);
	    ORDERED.add(BUILD);
	    ORDERED.add(BUILD_POST);
	}
    }
}
