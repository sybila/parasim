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
package org.sybila.parasim.model.ode;

import java.io.Serializable;

/**
 * Maps variable names to associated objects and vice versa.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 * @param <K>
 *            Type of objects associated with variables.
 *
 */
public interface VariableMapping<K> extends Serializable {

    /**
     * Returns objects for names.
     *
     * @param variableName
     *            Name of a variable.
     * @return Object associated with given variable name or <code>null</code>
     *         when there is no object associated with given name.
     */
    public K getKey(String variableName);

    /**
     * Returns names for objects.
     *
     * @param variableKey
     *            Object representing given variable.
     * @return Name of the variable or <code>null</code> when there is no name
     *         associated with the object.
     */
    public String getName(K variableKey);
}
