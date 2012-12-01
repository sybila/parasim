/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
