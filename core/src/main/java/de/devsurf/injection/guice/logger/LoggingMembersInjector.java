package de.devsurf.injection.guice.logger;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import com.google.inject.MembersInjector;

public class LoggingMembersInjector<T> implements MembersInjector<T> {
    private final Field _field;
    private final Logger _logger;

    LoggingMembersInjector(Field field) {
	this._field = field;
	this._logger = Logger.getLogger(field.getDeclaringClass().getName());
	field.setAccessible(true);
    }

    public void injectMembers(T t) {
	try {
	    _field.set(t, _logger);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	}
    }
}
