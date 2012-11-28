package org.sybila.parasim.extension.progresslogger.impl;

import org.apache.log4j.spi.LoggingEvent;
import org.sybila.parasim.core.Instance;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.extension.logging.LoggingListener;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindow;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindowRegistry;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProgressLoggingListener implements LoggingListener {

    @Inject
    private Instance<LoggerWindowRegistry> registry;

    @Override
    public void log(LoggingEvent event) {
        for (LoggerWindow window : registry.get()) {
            if (window.isDisplayable()) {
                window.log(event);
            }
        }
    }
}
