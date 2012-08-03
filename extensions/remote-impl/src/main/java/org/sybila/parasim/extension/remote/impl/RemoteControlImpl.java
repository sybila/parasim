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
            } finally {
                try {
                    hostControl.shutdown();
                } catch(Exception ignored) {
                }
            }
        }
    }

}
