/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.verification.stl;

/**
 * A simple abstract class representing a general unary operator.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class UnaryFormula extends AbstractFormula {

    private Formula subFormula;

    public UnaryFormula(Formula phi) {
        super(phi.getVariableIndexes());
        if (phi == null) {
            throw new IllegalArgumentException("Parameter phi is null.");
        }
        subFormula = phi;
    }

    @Override
    public int getArity() {
        return 1;
    }

    @Override
    public Formula getSubformula(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("Index must be 0.");
        }
        return subFormula;
    }

    @Override
    public int getStarNumber() {
        return subFormula.getStarNumber();
    }
}
