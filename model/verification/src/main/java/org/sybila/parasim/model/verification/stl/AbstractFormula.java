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

import java.util.Collection;
import java.util.Collections;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * Implements <code>equals</code> and <code>hashCode</code>.
 * Two formulae are equal if they represent the same predicate (denotated
 * by {@link #getType()}) and all their subformulae are equal (in given order).
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public abstract class AbstractFormula implements Formula {

    private final Collection<Integer> variableIndexes;

    public AbstractFormula(Collection<Integer> variableIndexes) {
        if (variableIndexes == null) {
            throw new IllegalArgumentException("The parameter [variableIndexes] is null.");
        }
        this.variableIndexes = Collections.unmodifiableCollection(variableIndexes);
    }

    @Override
    public Collection<Integer> getVariableIndexes() {
        return variableIndexes;
    }

    @Override
    public Element toXML(Document doc) {
        Element formula = getType().toXML(doc);
        for (int index = 0; index < getArity(); index++) {
            formula.appendChild(getSubformula(index).toXML(doc));
        }
        return formula;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true; //when referring to the same instance (convenience)
        }
        if (!(obj instanceof Formula)) {
            return false; //must implement Formula
        }
        Formula target = (Formula) obj;
        if (!getType().equals(target.getType())) {
            return false; //same type
        }
        if (getArity() != target.getArity()) {
            return false; //same arity (convenience)
        }
        for (int index = 0; index < getArity(); index++) { //testing of subformulae
            if (!getSubformula(index).equals(target.getSubformula(index))) {
                return false;
            }
        }
        return true; //non-equality ruled out
    }

    @Override
    public int hashCode() {
        final int prime = 41;
        int result = getType().ordinal();
        for (int index = 0; index < getArity(); index++) {
            result = result * prime + getSubformula(index).hashCode();
        }
        return result;
    }
}
