package org.sybila.parasim.model.verification.stl;

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
