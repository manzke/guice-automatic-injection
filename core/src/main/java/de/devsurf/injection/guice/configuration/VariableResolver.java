package de.devsurf.injection.guice.configuration;

import java.util.StringTokenizer;

import javax.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import de.devsurf.injection.guice.jsr330.Names;

public class VariableResolver {

    /**
     * The symbol that indicates a variable begin.
     */
    private static final String VAR_BEGIN = "$";

    /**
     * The symbol that separates the key name to the default value.
     */
    private static final String PIPE_SEPARATOR = "|";
    
    private static final String KEY_PREFIX = "${";

    /**
     * The Injector instance used to resolve variables.
     */
    @Inject
    private Injector injector;

    public String resolve(final String pattern) {
    	StringBuilder buffer = new StringBuilder();
    	
        int prev = 0;
        int pos;
        while ((pos = pattern.indexOf(VAR_BEGIN, prev)) >= 0) {
            if (pos > 0) {
                buffer.append(pattern.substring(prev, pos));
            }
            if (pos == pattern.length() - 1) {
            	buffer.append(VAR_BEGIN);
                prev = pos + 1;
            } else if (pattern.charAt(pos + 1) != '{') {
                if (pattern.charAt(pos + 1) == '$') {
                	buffer.append(VAR_BEGIN);
                    prev = pos + 2;
                } else {
                	buffer.append(pattern.substring(pos, pos + 2));
                    prev = pos + 2;
                }
            } else {
                int endName = pattern.indexOf('}', pos);
                if (endName < 0) {
                    throw new IllegalArgumentException("Syntax error in property: " + pattern);
                }
                StringTokenizer keyTokenizer = new StringTokenizer(pattern.substring(pos + 2, endName), PIPE_SEPARATOR);
                String key = keyTokenizer.nextToken().trim();
                String defaultValue = null;
                if (keyTokenizer.hasMoreTokens()) {
                    defaultValue = keyTokenizer.nextToken().trim();
                }

                try {
                    buffer.append(injector.getInstance(Key.get(String.class, Names.named(key))));
                } catch (Throwable e) {
                    if (defaultValue != null) {
                        buffer.append(defaultValue);
                    } else {
                        buffer.append(KEY_PREFIX).append(key).append('}');
                    }
                }

                prev = endName + 1;
            }
        }
        if (prev < pattern.length()) {
            buffer.append(pattern.substring(prev));
        }
        
        return buffer.toString();
    }

    public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AbstractModule() {
			@Override
			protected void configure() {
				bindConstant().annotatedWith(Names.named("variable.1")).to("feuer");
				bindConstant().annotatedWith(Names.named("variable.2")).to("frei");
				bindConstant().annotatedWith(Names.named("config.soap.protocol")).to("ftp");
				bindConstant().annotatedWith(Names.named("config.soap.ip")).to("1.1.1.1");
				bindConstant().annotatedWith(Names.named("config.soap.port")).to("9999");
				bindConstant().annotatedWith(Names.named("config.soap.app")).to("dynmaic");
				bindConstant().annotatedWith(Names.named("config.soap.client")).to("/henkel");
				bindConstant().annotatedWith(Names.named("config.soap.stage")).to("test");
			}
		});
		
		VariableResolver resolver = injector.getInstance(VariableResolver.class);
		System.out.println(resolver.resolve("${variable.1} ${variable.2}"));
		System.out.println(resolver.resolve("\"${variable.3| }\""));
		System.out.println(resolver.resolve("${config.soap.protocol|http}://${config.soap.ip|127.0.0.1}:${config.soap.port|12400}/${config.soap.app|configuration}${config.soap.client| }/soap/optional.xml?stage=${config.soap.stage|default}"));
	}

}
