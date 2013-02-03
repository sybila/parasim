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
package org.sybila.parasim.extension.remote.impl;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.extension.remote.api.RemoteControl;
import org.sybila.parasim.extension.remote.api.RemoteHostControl;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteControlImpl implements RemoteControl {

    private final Map<URI, RemoteHostControl> hostControls;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteControlImpl.class);

    public RemoteControlImpl(Collection<RemoteHostControl> hostControls) {
        Validate.notNull(hostControls);
        Map<URI, RemoteHostControl> hostControlsToSave = new HashMap<>(hostControls.size());
        for (RemoteHostControl hostControl: hostControls) {
            if (hostControlsToSave.containsKey(hostControl.getHost())) {
                throw new IllegalArgumentException("Can't register host <"+hostControl.getHost()+"> twice.");
            }
            hostControlsToSave.put(hostControl.getHost(), hostControl);
        }
        this.hostControls = Collections.unmodifiableMap(hostControlsToSave);
    }

    @Override
    public Collection<RemoteHostControl> getHostControls() {
        return hostControls.values();
    }

    @Override
    public RemoteRunningStatus getRunningStatus(boolean ping) {
        RemoteRunningStatus status = RemoteRunningStatus.NONE;
        for (RemoteHostControl hostControl: hostControls.values()) {
            if (hostControl.isRunning(ping)) {
                if (status == RemoteRunningStatus.NONE) {
                    status = RemoteRunningStatus.ALL;
                }
            } else {
                if (status == RemoteRunningStatus.ALL) {
                    status = RemoteRunningStatus.SOME;
                }
            }
        }
        return status;
    }

    @Override
    public void shutdown() {
        for (RemoteHostControl hostControl: hostControls.values()) {
            if (hostControl.isRunning(true)) {
                try {
                    hostControl.shutdown();
                } catch(Exception e) {
                    LOGGER.warn("The remote control for host < " + hostControl.getHost() + "> can't be closed.", e);
                }
            }
        }
    }

    @Override
    public void start(long time, TimeUnit unit) {
        for (RemoteHostControl hostControl: hostControls.values()) {
            try {
                if (!hostControl.isRunning(true)) {
                    hostControl.start(time, unit);
                }
            } catch(IOException e) {
                LOGGER.warn("The remote control for host < " + hostControl.getHost() + "> can't be started.", e);
            }
        }
    }

}
