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

import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DefaultOdeSystem extends AbstractOdeSystem {

    private OdeSystemEncoding encoding;
    private List<Variable> variables;

    public DefaultOdeSystem(OdeSystemEncoding encoding, List<Variable> variables) {
        if (encoding == null) {
            throw new IllegalArgumentException("The parameter encoding is null.");
        }
        this.encoding = encoding;
        this.variables = variables;
    }

    public DefaultOdeSystem(OdeSystemEncoding encoding) {
        this(encoding, null);
    }

    public int dimension() {
        return encoding.countVariables();
    }

    public OdeSystemEncoding encoding() {
        return encoding;
    }

    public Variable getVariable(int dimension) {
        if (variables == null) {
            throw new UnsupportedOperationException("Not supported yet.");
        } else {
            return variables.get(dimension);
        }
    }
}
