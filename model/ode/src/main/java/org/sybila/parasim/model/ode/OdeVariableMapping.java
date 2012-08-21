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

import org.sybila.parasim.model.math.Variable;

/**
 * {@link PointVariableMapping} which is constructed from an {@link OdeSystem}.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class OdeVariableMapping implements PointVariableMapping {
    private int dimension;
    private DoubleMap<Integer> mapping = new DoubleMap<>();

    /**
     * @param source System from which the variable names are derived.
     * @throws IllegalArgumentException when there is duplicate variable name.
     */
    public OdeVariableMapping(OdeSystem source) {
        dimension = source.dimension();
        for (int index = 0; index < dimension; index++) {
            Variable var = source.getVariable(index);
            if (!mapping.put(index, var.getName())) {
                mapping.clear();
                throw new IllegalArgumentException("Duplicate variable name.");
            }
        }
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public String getName(Integer variableKey) {
        return mapping.getName(variableKey);
    }

    @Override
    public Integer getKey(String variableName) {
        return mapping.getKey(variableName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OdeVariableMapping)) return false;
        OdeVariableMapping target = (OdeVariableMapping) obj;
        if (dimension != target.dimension) return false;
        return mapping.equals(target.mapping);
    }

    @Override
    public int hashCode() {
        final int prime = 53;
        return prime*mapping.hashCode()+dimension;
    }
}
