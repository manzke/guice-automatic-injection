package de.devsurf.injection.guice.serviceloader;

import java.util.Iterator;
import java.util.ServiceLoader;

import javax.inject.Inject;

import com.google.inject.Provider;

public final class SingleServiceLoaderProvider<T> implements Provider<T> {

	private final Class<T> type;

	@Inject
	com.google.inject.Injector injector;

	public SingleServiceLoaderProvider(Class<T> type) {
		this.type = type;
	}

	/*
	 * Generated classes can't be used with AOP, because only instances created
	 * by Guice are extended with AOP. Maybe this will be fixed later in Guice.
	 * @see com.google.inject.Provider#get()
	 */
	@Override
	public T get() {
		ServiceLoader<T> services = ServiceLoader.load(type);
		
		Iterator<T> iterator = services.iterator();
		if(iterator.hasNext()){
			T instance = iterator.next();
			injector.injectMembers(instance);
			return instance;
		}
		
		return null;
	}

	public static <T> Provider<T> of(Class<T> type) {
		return new SingleServiceLoaderProvider<T>(type);
	}
}
