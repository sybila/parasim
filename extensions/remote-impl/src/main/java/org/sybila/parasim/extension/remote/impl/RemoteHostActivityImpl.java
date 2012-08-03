package org.sybila.parasim.extension.remote.impl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.extension.remote.api.RemoteHostActivity;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class RemoteHostActivityImpl implements RemoteHostActivity {

    private final AtomicLong time;
    private final long timeout;
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteHostActivityImpl.class);

    public RemoteHostActivityImpl(long time, TimeUnit unit) {
        Validate.isTrue(time >= 0, "The time can't be a negative number.");
        Validate.notNull(unit);
        this.timeout = unit.toMillis(time);
        this.time = new AtomicLong(this.timeout + System.currentTimeMillis());
    }

    @Override
    public void activate() {
        time.addAndGet(timeout);
    }

    @Override
    public void activate(long time, TimeUnit unit) {
        this.time.addAndGet(unit.toMillis(time));
    }

    @Override
    public void waitUntilFinished() {
        long now = 0;
        long expected;
        while ((now = System.currentTimeMillis()) < (expected = time.get())) {
            try {
                LOGGER.debug("waiting for " + (expected - now) + " ms");
                Thread.sleep(expected - now);
            } catch(InterruptedException ignored) {
            }
        }
    }

}
