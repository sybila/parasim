/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.core.impl.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.sybila.parasim.core.api.ServiceRepository;
import org.sybila.parasim.core.spi.logging.LoggingListener;

public class ServiceAppender extends AppenderSkeleton {

    private static volatile ServiceRepository serviceRepository;

    private volatile boolean enabled = true;

    @Override
    protected void append(LoggingEvent event) {
        if (enabled && serviceRepository != null) {
            for (LoggingListener listener: serviceRepository.service(LoggingListener.class)) {
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

    public static void setServiceRepository(ServiceRepository serviceRepository) {
        ServiceAppender.serviceRepository = serviceRepository;
    }

}
