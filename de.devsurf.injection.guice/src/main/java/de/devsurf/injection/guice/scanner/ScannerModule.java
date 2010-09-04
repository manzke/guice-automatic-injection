package de.devsurf.injection.guice.scanner;

import java.io.IOException;

import com.google.inject.Binder;
import com.google.inject.Inject;

import de.devsurf.injection.guice.DynamicModule;
import de.devsurf.injection.guice.annotations.AutoBind;
import de.devsurf.injection.guice.annotations.GuiceModule;

public class ScannerModule implements DynamicModule {
	private ClasspathScanner _scanner;
	
	@Inject
	public ScannerModule(ClasspathScanner scanner) {
		_scanner = scanner;
	}

	@Override
	public void configure(Binder binder) {	
		_scanner.addAnnotationListener(new GuiceModule.GuiceModuleListener(binder));
		_scanner.addAnnotationListener(new AutoBind.AutoBindListener(binder));

		try {
			_scanner.scan();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
