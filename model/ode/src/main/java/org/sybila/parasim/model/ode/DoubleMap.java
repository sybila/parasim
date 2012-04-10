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
package org.sybila.parasim.model.ode;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple implementation of {@link VariableMapping} using two maps. Implements
 * methods for element operation.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 * @param <K>
 *            Type of objects associated with variables (see
 *            {@link VariableMapping}).
 */
public class DoubleMap<K> implements VariableMapping<K> {
    private Map<K, String> keyMap = new HashMap<K, String>();
    private Map<String, K> nameMap = new HashMap<String, K>();

    /**
     * Creates new empty {@link VariableMapping}.
     */
    public DoubleMap() {
    }

    /**
     * Creates {@link VariableMapping} from existing Map.
     *
     * @param map
     *            Input map. Must be injective, i.e. there are no two keys with
     *            the same value.
     * @throws IllegalArgumentException
     *             when the map is not injective.
     */
    public DoubleMap(Map<? extends K, ? extends String> map) {
        for (Map.Entry<? extends K, ? extends String> entry : map.entrySet()) {
            if (!put(entry.getKey(), entry.getValue())) {
                clear();
                throw new IllegalArgumentException(
                        "Input map has to be injective.");
            }
        }
    }

    public boolean containsKey(K key) {
        return keyMap.containsKey(key);
    }

    public boolean containsName(String name) {
        return nameMap.containsKey(name);
    }

    /**
     * Adds new entry to mapping.
     *
     * @return <code>true</code> when the entry was added successfully,
     *         <code>false</code> otherwise (i.e. <code>key</code> or
     *         <code>name</code> is already in the mapping).
     */
    public boolean put(K key, String name) {
        if (containsKey(key))
            return false;
        if (containsName(name))
            return false;
        keyMap.put(key, name);
        nameMap.put(name, key);
        return true;
    }

    public K removeByName(String name) {
        if (!containsName(name))
            return null;
        K key = getKey(name);
        keyMap.remove(key);
        nameMap.remove(name);
        return key;
    }

    public String removeByKey(K key) {
        if (!containsKey(key))
            return null;
        String name = getName(key);
        keyMap.remove(key);
        nameMap.remove(name);
        return name;
    }

    public void clear() {
        keyMap.clear();
        nameMap.clear();
    }

    @Override
    public K getKey(String variableName) {
        return nameMap.get(variableName);
    }

    @Override
    public String getName(K variableKey) {
        return keyMap.get(variableKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof DoubleMap<?>)) return false;
        return keyMap.equals(((DoubleMap<?>)obj).keyMap);
    }

    @Override
    public int hashCode() {
        return keyMap.hashCode();
    }

}
