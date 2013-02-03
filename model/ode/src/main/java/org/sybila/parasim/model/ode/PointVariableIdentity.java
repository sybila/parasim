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
package org.sybila.parasim.model.ode;

/**
 * Maps dimensions to their string representation. To be used
 * when there is no better mapping available.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PointVariableIdentity implements PointVariableMapping {

    private boolean checkInt(Integer input) {
        return !(input < 0);
    }

    @Override
    public int getDimension() {
        throw new UnsupportedOperationException("Dimension of PointVariableIdentity is not specified by default.");
    }

    @Override
    public Integer getKey(String variableName) {
        Integer result;
        try {
            result = new Integer(variableName);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (checkInt(result)) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getName(Integer variableKey) {
        if (checkInt(variableKey)) {
            return variableKey.toString();
        } else {
            return null;
        }
    }
}
