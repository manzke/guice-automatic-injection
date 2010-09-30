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
package de.devsurf.injection.guice.configuration.dynamic;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class Formatter implements Provider<String> {

    private static final String VAR_BEGIN = "$";

    private final List<Appender> appenders = new ArrayList<Appender>();

    private boolean containsKeys = false;

    @Inject
    private Injector injector;

    public Formatter(final String pattern) {
        int prev = 0;
        int pos;
        while ((pos = pattern.indexOf(VAR_BEGIN, prev)) >= 0) {
            if (pos > 0) {
                this.appenders.add(new TextAppender(pattern.substring(prev, pos)));
            }
            if (pos == pattern.length() - 1) {
                this.appenders.add(new TextAppender(VAR_BEGIN));
                prev = pos + 1;
            } else if (pattern.charAt(pos + 1) != '{') {
                if (pattern.charAt(pos + 1) == '$') {
                    this.appenders.add(new TextAppender(VAR_BEGIN));
                    prev = pos + 2;
                } else {
                    this.appenders.add(new TextAppender(pattern.substring(pos, pos + 2)));
                    prev = pos + 2;
                }
            } else {
                int endName = pattern.indexOf('}', pos);
                if (endName < 0) {
                    throw new IllegalArgumentException("Syntax error in property: " + pattern);
                }
                String key = pattern.substring(pos + 2, endName);
                this.appenders.add(new KeyAppender(key));
                prev = endName + 1;
                this.containsKeys = true;
            }
        }
        if (prev < pattern.length()) {
            this.appenders.add(new TextAppender(pattern.substring(prev)));
        }
    }

    /**
     * Return true, if the text contains at least one key in the ${} pattern,
     * false otherwise.
     *
     * @return true, if the text contains at least one key in the ${} pattern,
     *         false otherwise.
     */
    public boolean containsKeys() {
        return this.containsKeys;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    /**
     * {@inheritDoc}
     */
    public String get() {
        StringBuilder buffer = new StringBuilder();
        for (Appender appender : this.appenders) {
            appender.append(buffer, this.injector);
        }
        return buffer.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.appenders.toString();
    }

}

