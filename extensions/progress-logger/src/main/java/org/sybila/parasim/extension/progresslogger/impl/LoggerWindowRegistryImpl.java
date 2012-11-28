package org.sybila.parasim.extension.progresslogger.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindow;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindowRegistry;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class LoggerWindowRegistryImpl implements LoggerWindowRegistry {

    private final List<LoggerWindow> windows = new ArrayList<>();

    @Override
    public void addWindow(LoggerWindow target) {
        windows.add(target);
    }

    @Override
    public Iterator<LoggerWindow> iterator() {
        return Collections.unmodifiableList(windows).iterator();
    }

    @Override
    public void removeWindow(LoggerWindow target) {
        windows.remove(target);
    }
}
