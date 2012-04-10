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
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OctaveOdeSystem {

    private OdeSystemEncoding encoding;
    private String octaveString;

    public OctaveOdeSystem(OdeSystemEncoding encoding) {
        if (encoding == null) {
            throw new IllegalArgumentException("The parameter encoding is null.");
        }
        this.encoding = encoding;
    }

    public String octaveName() {
        return "f";
    }

    public String octaveString() {
        if (octaveString == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("function xdot = f(x, t) ");
            builder.append("xdot = zeros(").append(encoding.countVariables()).append(", 1);");
            for (int v = 0; v < encoding.countVariables(); v++) {
                builder.append("xdot(").append(v + 1).append(") = ");
                for (int c = 0; c < encoding.countCoefficients(v); c++) {
                    if (c != 0) {
                        builder.append(" + ");
                    }
                    builder.append(encoding.coefficient(v, c) >= 0 ? encoding.coefficient(v, c) : "(" + encoding.coefficient(v, c) + ")");
                    for (int f = 0; f < encoding.countFactors(v, c); f++) {
                        builder.append("*").append("x(").append(encoding.factor(v, c, f) + 1).append(")");
                    }
                }
                builder.append("; ");
            }
            builder.append("endfunction");
            octaveString = builder.toString();
        }
        return octaveString;
    }
}
