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
package org.sybila.parasim.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapInstanceStorage implements InstanceStorage {

    private Map<Class<?>, Object> instances;

    public MapInstanceStorage(Map<Class<?>, Object> instances) {
        if (instances == null) {
            throw new IllegalArgumentException("The parameter [instances] is null.");
        }
        this.instances = instances;
    }

    public MapInstanceStorage() {
        this(new ConcurrentHashMap<Class<?>, Object>());
    }

    public <T> InstanceStorage add(Class<T> type, T value) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter [value] is null.");
        }
        instances.put(type, value);
        return this;
    }

    public InstanceStorage clear() {
        instances.clear();
        return this;
    }

    public <T> T get(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        return type.cast(instances.get(type));
    }

}