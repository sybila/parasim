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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Until operator.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class UntilFormula extends BinaryFormula implements TemporalFormula {

    private FormulaInterval interval;

    public UntilFormula(Formula phi1, Formula phi2, FormulaInterval interval) {
        super(phi1, phi2);
        if (interval == null) {
            throw new IllegalArgumentException("Parameter interval is null.");
        }
        this.interval = interval;
    }

    @Override
    public float getTimeNeeded() {
        return java.lang.Math.max(getSubformula(0).getTimeNeeded(),
                getSubformula(1).getTimeNeeded()) + interval.getUpperBound();
    }

    @Override
    public FormulaInterval getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "( " + getSubformula(0).toString() + " ) U_" + interval.toString() + " ( " + getSubformula(1).toString() + " )";
    }

    @Override
    public FormulaType getType() {
        return FormulaType.UNTIL;
    }

    @Override
    public Element toXML(Document doc) {
        Element target = super.toXML(doc);
        target.insertBefore(getInterval().toXML(doc), target.getFirstChild());
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof TemporalFormula)) {
            return false;
        }
        return interval.equals(((TemporalFormula) obj).getInterval());
    }

    @Override
    public int hashCode() {
        final int prime = 41;
        int result = super.hashCode();
        result = result * prime + getInterval().hashCode();
        return result;
    }

}
