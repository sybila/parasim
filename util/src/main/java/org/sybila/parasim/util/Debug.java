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
package org.sybila.parasim.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.Validate;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class Debug {

    private static ThreadLocal<Map<String, Long>> times = new MapForMeasurements();
    private static ThreadLocal<Map<String, Long>> memories = new MapForMeasurements();

    private static final String DEFAULT_NAME = "default";

    private Debug() {
    }

    public static void reset() {
        times = new MapForMeasurements();
        memories = new MapForMeasurements();
    }

    public static long memory() {
        return memory(DEFAULT_NAME);
    }

    public static long memory(String name) {
        Validate.notNull(name);
        long now = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        Long previous = memories.get().remove(name);
        if (previous != null) {
            return now - previous;
        } else {
            memories.get().put(name, now);
            return 0;
        }
    }

    public static long time() {
        return time(DEFAULT_NAME);
    }

    public static long time(String name) {
        Validate.notNull(name);
        long now = System.nanoTime();
        synchronized(name) {
            Long previous = times.get().remove(name);
            if (previous != null) {
                return now - previous;
            } else {
                times.get().put(name, now);
                return 0;
            }
        }
    }

    private static class MapForMeasurements extends ThreadLocal<Map<String, Long>> {

        @Override
        protected Map<String, Long> initialValue() {
            return new HashMap<>();
        }

    }

}
