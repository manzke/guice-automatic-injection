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
	BOOT, BINDING, INSTALL, BUILD, IGNORE;

	public static final List<BindingStage> ORDERED = new LinkedList<BindingStage>();

	static {
	    ORDERED.add(BOOT);
	    ORDERED.add(BINDING);
	    ORDERED.add(INSTALL);
	    ORDERED.add(BUILD);
	}
    }
}
