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
package org.sybila.parasim.model.verification.stl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Signal-value freeze operator on formula.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FreezeFormula extends UnaryFormula {

    private int index;

    /**
     * Create formula by freezing a subformula. Specifies freeze index.
     *
     * @param phi Subformula.
     * @param index Freeze index.
     */
    public FreezeFormula(Formula phi, int index) {
        super(phi);
        if (index <= 0) {
            throw new IllegalArgumentException("Frozen time index has to be greater than zero.");
        }
        this.index = index;
    }

    @Override
    public FormulaType getType() {
        return FormulaType.FREEZE;
    }

    @Override
    public float getTimeNeeded() {
        return getSubformula(0).getTimeNeeded();
    }

    /**
     * @return Freeze index.
     */
    public int getFreezeIndex() {
        return index;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder("*");
        build.append(index);
        build.append("(");
        build.append(getSubformula(0).toString());
        build.append(")");
        return build.toString();
    }

    @Override
    public Element toXML(Document doc) {
        Element target = super.toXML(doc);
        target.setAttribute(FormulaFactory.FREEZE_INDEX_NAME, Integer.toString(getFreezeIndex()));
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof FreezeFormula)) {
            return false;
        }
        return getFreezeIndex() == ((FreezeFormula) obj).getFreezeIndex();
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 37 + getFreezeIndex();
    }
}