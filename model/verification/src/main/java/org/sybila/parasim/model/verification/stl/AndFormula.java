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
package org.sybila.parasim.model.verification.stl;

/**
 * The conjunction of two formulas.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class AndFormula extends BinaryFormula {

    public AndFormula(Formula phi1, Formula phi2) {
        super(phi1, phi2);
    }

    @Override
    public float getTimeNeeded() {
        return java.lang.Math.max(getSubformula(0).getTimeNeeded(),
                getSubformula(1).getTimeNeeded());
    }

    @Override
    public String toString() {
        return "( " + getSubformula(0).toString() + " ) && ( " + getSubformula(1).toString() + " )";
    }

    @Override
    public FormulaType getType() {
        return FormulaType.AND;
    }
}
