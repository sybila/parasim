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
