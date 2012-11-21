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
package org.sybila.parasim.core.extension.logging;

import java.util.Collection;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

public class ServiceAppender extends AppenderSkeleton {

    private static volatile Collection<LoggingListener> listeners;

    private volatile boolean enabled = true;

    @Override
    protected void append(LoggingEvent event) {
        if (enabled && listeners != null) {
            for (LoggingListener listener: listeners) {
                listener.log(event);
            }
        }
    }

    @Override
    public void close() {
        enabled = false;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    static void setListeners(Collection<LoggingListener> listeners) {
        ServiceAppender.listeners = listeners;
    }

}
