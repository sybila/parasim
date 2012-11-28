package org.sybila.parasim.extension.progresslogger.api;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface LoggerWindowRegistry extends Iterable<LoggerWindow> {

    void addWindow(LoggerWindow target);

    void removeWindow(LoggerWindow target);
}
