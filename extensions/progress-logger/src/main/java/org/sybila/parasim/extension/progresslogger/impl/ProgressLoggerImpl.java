package org.sybila.parasim.extension.progresslogger.impl;

import org.apache.commons.lang3.Validate;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindow;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindowRegistry;
import org.sybila.parasim.extension.progresslogger.api.ProgressLogger;
import org.sybila.parasim.extension.progresslogger.view.LoggerWindowImpl;
import org.sybila.parasim.extension.progresslogger.view.StyledLoggerOutput;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProgressLoggerImpl implements ProgressLogger {

    private final LoggerWindowRegistry windows;

    public ProgressLoggerImpl(LoggerWindowRegistry windows) {
        Validate.notNull(windows);
        this.windows = windows;
    }

    @Override
    public LoggerWindow getLoggerWindow() {
        LoggerWindowImpl result = new LoggerWindowImpl(new StyledLoggerOutput());
        windows.addWindow(result);
        return result;
    }

    @Override
    public void removeLoggerWindow(LoggerWindow target) {
        windows.removeWindow(target);
    }
}
