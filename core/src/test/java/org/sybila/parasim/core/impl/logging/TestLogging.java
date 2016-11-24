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
package org.sybila.parasim.core.impl.logging;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.spi.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.core.spi.logging.LoggingListener;
import org.sybila.parasim.core.test.ParasimTest;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestLogging extends ParasimTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestLogging.class);

    private static int count = 0;

    @Test
    public void testLoggingListeners() throws Exception {
        LOGGER.info("test message");
        Assert.assertNotEquals(count, 0);
    }

    @Override
    protected Map<Object, Class<?>> getServices() {
        Map<Object, Class<?>> services = new HashMap<>();
        services.put(new TestLoggingListener(), LoggingListener.class);
        return services;
    }

    public static class TestLoggingListener implements LoggingListener {

        @Override
        public void log(LoggingEvent event) {
            count++;
        }

    }

}
