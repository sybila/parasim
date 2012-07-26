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
package org.sybila.parasim.core.extension.lifecycle.impl;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.core.extension.lifecycle.api.LifecycleRegistry;
import org.sybila.parasim.core.extension.lifecycle.spi.Configurator;
import org.sybila.parasim.core.extension.lifecycle.spi.Constructor;
import org.sybila.parasim.core.extension.lifecycle.spi.Destructor;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LifecycleRegistryImpl implements LifecycleRegistry {

    private Map<Class<?>, RegistryEntry> entries = new HashMap<>();

    @Override
    public <T> Configurator<T, ?> loadConfigurator(Class<T> key) {
        RegistryEntry entry = entries.get(key);
        return entry == null ? null : (Configurator<T, ?>) entry.configurator;
    }

    @Override
    public <T> Constructor<T, ?> loadConstructor(Class<T> key) {
        RegistryEntry entry = entries.get(key);
        return entry == null ? null : (Constructor<T, ?>) entry.constructor;
    }

    @Override
    public <T> Destructor<T> loadDestructor(Class<T> key) {
        RegistryEntry entry = entries.get(key);
        return entry == null ? null : (Destructor<T>) entry.destructor;
    }

    @Override
    public void storeConfigurator(Class<?> key, Configurator<?, ?> configurator) {
        getEntry(key).configurator = configurator;
    }

    @Override
    public void storeConstructor(Class<?> key, Constructor<?, ?> constructor) {
        getEntry(key).constructor = constructor;
    }

    @Override
    public void storeDestructor(Class<?> key, Destructor<?> destructor) {
        getEntry(key).destructor = destructor;
    }

    private RegistryEntry getEntry(Class<?> key) {
        RegistryEntry entry = entries.get(key);
        if (entry == null) {
            entry = new RegistryEntry();
            entries.put(key, entry);
        }
        return entry;
    }

    private static class RegistryEntry {

        public Configurator<?,?> configurator;

        public Constructor<?, ?> constructor;

        public Destructor<?> destructor;

    }

}
