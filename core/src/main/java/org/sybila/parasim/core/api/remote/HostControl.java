/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.core.api.remote;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.rmi.Remote;

/**
 * @author <a href="mailto:xpapous1@mail.muni.cz">Jan Papousek</a>
 */
public interface HostControl {

    /**
     * @return URL of the remote host
     */
    URI getHost();

    /**
     * Check whether the host is running.
     *
     * @param ping if false, the return value is resolved from cache,
     */
    boolean isRunning(boolean ping);

    /**
     * Lookup remote object.
     *
     * @param <T> type of the remote object
     * @param clazz type of the remote object
     * @param qualifier use ({@link org.sybila.parasim.core.annotations.Default} if you don't know which qualifer should be used
     * @return proxy for the remote object
     * @throws IOException if there is an error during lookup process
     */
    <T extends Remote> T lookup(Class<T> clazz, Class<? extends Annotation> qualifier) throws IOException;

}
