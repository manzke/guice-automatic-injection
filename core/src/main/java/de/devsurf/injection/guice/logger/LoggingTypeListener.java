package de.devsurf.injection.guice.logger;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class LoggingTypeListener implements TypeListener {
    @Override
    public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
	for (Field field : typeLiteral.getRawType().getDeclaredFields()) {
	    if (field.getType() == Logger.class && field.isAnnotationPresent(InjectLogger.class)) {
		typeEncounter.register(new LoggingMembersInjector<I>(field));
	    }
	}
    }
}
