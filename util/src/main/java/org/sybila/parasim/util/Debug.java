package org.sybila.parasim.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.Validate;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class Debug {

    private static Map<String, Long> timers = new ConcurrentHashMap<>();

    private Debug() {
    }

    private static long timer(String name) {
        Validate.notNull(name);
        long now = System.currentTimeMillis();
        synchronized(name) {
            Long previousTime = timers.get(name);
            timers.put(name, now);
            if (previousTime != 0) {
                return now - previousTime;
            } else {
                return 0;
            }
        }
    }

}
