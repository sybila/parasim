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

/**
 * {@link PointVariableMapping} which is constructed from an {@link OdeSystem}.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class OdeVariableMapping implements PointVariableMapping {

    private final OdeSystem odeSystem;

    /**
     * @param source System from which the variable names are derived.
     * @throws IllegalArgumentException when there is duplicate variable name.
     */
    public OdeVariableMapping(OdeSystem source) {
        this.odeSystem = source;
    }

    @Override
    public int getDimension() {
        return odeSystem.dimension();
    }

    @Override
    public String getName(Integer variableKey) {
        return odeSystem.isVariable(variableKey) ? odeSystem.getVariable(variableKey).getName() : odeSystem.getParameter(variableKey).getName();
    }

    @Override
    public Integer getKey(String variableName) {
        return odeSystem.getVariables().get(variableName).getIndex();
    }
}
