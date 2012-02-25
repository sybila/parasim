package org.sybila.parasim.model.verification.stl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Globaly G operator.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class GlobalyFormula extends UnaryFormula implements TemporalFormula {

    private FormulaInterval interval;

    public GlobalyFormula(Formula phi, FormulaInterval interval) {
        super(phi);
        if (interval == null) {
            throw new IllegalArgumentException("Parameter interval is null.");
        }
        this.interval = interval;
    }

    @Override
    public float getTimeNeeded() {
        return getSubformula(0).getTimeNeeded() + interval.getUpperBound();
    }

    @Override
    public FormulaInterval getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "G_" + interval.toString() + "( " + getSubformula(0).toString() + " )";
    }

    @Override
    public FormulaType getType() {
        return FormulaType.GLOBALLY;
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
            return false; //type and subformulae
        }
        if (!(obj instanceof TemporalFormula)) {
            return false; //is temporal formula
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
