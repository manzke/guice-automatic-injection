package de.devsurf.injection.guice.serviceloader;

import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class ModuleLoader<M extends Module> extends AbstractModule {
	private Logger _logger = Logger.getLogger(ServiceLoaderModule.class.getName());
	
    private final Class<M> type;

    public ModuleLoader(Class<M> type) {
        this.type = type;
    }

    public static <M extends Module> ModuleLoader<M> of(Class<M> type) {
        return new ModuleLoader<M>(type);
    }

    @Override
    protected void configure() {
        ServiceLoader<M> modules = ServiceLoader.load(type);
        for (Module module : modules) {
            try {
				install(module);
			} catch (Exception e) {
				_logger.log(Level.WARNING, "Module can't be installed, because an Exception was raised. "+e.getMessage(), e);
			}
        }
    }
}
