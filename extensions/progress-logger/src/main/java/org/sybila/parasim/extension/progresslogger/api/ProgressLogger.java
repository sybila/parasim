package org.sybila.parasim.extension.progresslogger.api;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ProgressLogger {

    public LoggerWindow getLoggerWindow();

    public void removeLoggerWindow(LoggerWindow target);
}
