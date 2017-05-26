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
package org.sybila.parasim.core.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.annotation.Observes;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.core.impl.ManagerImpl;
import org.sybila.parasim.core.impl.ServiceStorage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class ParasimTest {

    private Manager manager;

    @BeforeMethod(dependsOnGroups="setup")
    protected void createManager() throws Exception {
        beforeManagerCreated();
        File config = getConfiguration();
        if (config != null) {
            System.setProperty("parasim.config.file", config.getAbsolutePath());
        }
        manager = ManagerImpl.create(mergeExtensions(getTestExtensions(), getExtensions()));
        for (Entry<Object, Class<?>> entry: getServices().entrySet()) {
            storeService(entry.getValue(), entry.getKey());
        }
    }

    @BeforeMethod(dependsOnMethods="createManager")
    protected void startManager() {
        manager.start();
    }

    @AfterMethod
    protected void destroyManager() throws Exception {
        if (manager != null) {
            manager.destroy();
        }
    }

    @AfterMethod(dependsOnMethods="destroyManager")
    protected void cleanEventCounts() {
        EventCounter.counts.clear();
    }

    protected Class<?>[] getExtensions() {
        return new Class<?>[0];
    }

    protected Map<Object, Class<?>> getServices() {
        return Collections.EMPTY_MAP;
    }

    protected Manager getManager() {
        return manager;
    }

    protected int getNumberOfEvents(Class<?> clazz) {
        Integer count = EventCounter.counts.get(clazz);
        return count == null ? 0: count;
    }

    protected File getConfiguration() throws IOException {
        Enumeration<URL> configs = ParasimTest.class.getClassLoader().getResources("parasim.xml");
        while (configs.hasMoreElements()) {
            return new File(configs.nextElement().getFile());
        }
        return null;
    }

    protected void beforeManagerCreated() {
    }

    private Class<?>[] getTestExtensions() {
        return new Class<?>[] { EventCounter.class };
    }

    private <T> void storeService(Class<T> service, Object impl) {
        getManager().resolve(ServiceStorage.class, Default.class).store(service, (T) impl);
    }

    private static Class<?>[] mergeExtensions(Class<?>[] first, Class<?>... second) {
        Class<?>[] result = new Class<?>[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public static class EventCounter {
        private static Map<Class<?>, Integer> counts = new HashMap<>();

        public void count(@Observes Object event) {
            Integer count = counts.get(event.getClass());
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            counts.put(event.getClass(), count);
        }
    }

}
