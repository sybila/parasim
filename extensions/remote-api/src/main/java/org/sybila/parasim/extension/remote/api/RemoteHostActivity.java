package org.sybila.parasim.extension.remote.api;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface RemoteHostActivity extends Serializable {

    /**
     * Extend the time of activity for the default amount of time.
     */
    void activate();

    /**
     * Extend the time of activity for the given amount of time
     * @param time
     * @param unit
     */
    void activate(long time, TimeUnit unit);

    /**
     * Wait until the time of activity finishes
     */
    void waitUntilFinished();

}
