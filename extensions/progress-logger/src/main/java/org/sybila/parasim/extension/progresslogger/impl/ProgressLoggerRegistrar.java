package org.sybila.parasim.extension.progresslogger.impl;

import org.sybila.parasim.core.Event;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindowRegistry;
import org.sybila.parasim.extension.progresslogger.api.ProgressLogger;
import org.sybila.parasim.extension.progresslogger.api.ProgressLoggerRegistered;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProgressLoggerRegistrar {

    @Inject
    private Event<ProgressLoggerRegistered> event;

    @Provide
    public LoggerWindowRegistry registerRegistry() {
        return new LoggerWindowRegistryImpl();
    }

    @Provide
    public ProgressLogger register(LoggerWindowRegistry windowRegistry) {
        fireEvent();
        return new ProgressLoggerImpl(windowRegistry);
    }

    private void fireEvent() {
        event.fire(new ProgressLoggerRegistered());
    }
}
