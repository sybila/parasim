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
package org.sybila.parasim.extension.remote.api;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * This class represents a control mechanism providing you a way to manipulate
 * with remote instances of {@link org.sybila.parasim.extension.remote.api.RemoteManager}
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface RemoteControl {

    /**
     * Returns a collection of remote controls for hosts managing by this remote
     * control
     *
     * @return a collection of remote controls
     */
    Collection<RemoteHostControl> getHostControls();

    /**
     * Returns a status of the remote instances of {@link org.sybila.parasim.extension.remote.api.RemoteManager}
     * @param ping if true, the remote control tries to ping all remote hosts,
     * if false, the status is resolved from cache
     * @return a status of the remote instances of {@link org.sybila.parasim.extension.remote.api.RemoteManager}
     */
    RemoteRunningStatus getRunningStatus(boolean ping);

    /**
     * Shutdown the remote control
     */
    void shutdown();

    /**
     * Start the remote control. It means that the remote control tries to
     * start remote instance of {@link org.sybila.parasim.extension.remote.api.RemoteManager}.
     *
     * @param timeout the remote instance {@link org.sybila.parasim.extension.remote.api.RemoteManager}
     * stays running for the given amount of time of inactivity
     * @param unit time unit for timeout
     */
    void start(long timeout, TimeUnit unit);

    public enum RemoteRunningStatus {
        ALL, SOME, NONE;
    }

}
