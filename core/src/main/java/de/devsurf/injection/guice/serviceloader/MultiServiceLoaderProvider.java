package de.devsurf.injection.guice.serviceloader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.inject.Inject;

import com.google.inject.Injector;
import com.google.inject.Provider;

public final class MultiServiceLoaderProvider<T> implements Provider<T[]> {

	private final Class<T> type;

	@Inject
	Injector injector;

	public MultiServiceLoaderProvider(Class<T> type) {
		this.type = type;
	}

	/*
	 * Generated classes can't be used with AOP, because only instances created
	 * by Guice are extended with AOP. Maybe this will be fixed later in Guice.
	 * @see com.google.inject.Provider#get()
	 */
	@SuppressWarnings( { "unchecked" })
	@Override
	public T[] get() {
		List<T> instances = new ArrayList<T>();
		ServiceLoader<T> services = ServiceLoader.load(type);
		
		for(T t : services){
			injector.injectMembers(t);
			instances.add(t);
		}
		
		return instances.toArray((T[]) Array.newInstance(type, instances.size()));
	}

	public static <T> Provider<T[]> of(Class<T> type) {
		return new MultiServiceLoaderProvider<T>(type);
	}
}
