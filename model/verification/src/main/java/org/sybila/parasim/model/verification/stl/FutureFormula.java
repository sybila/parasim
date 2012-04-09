package org.sybila.parasim.model.verification.stl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Future or Finally F operator.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FutureFormula extends UnaryFormula implements TemporalFormula {

    private FormulaInterval interval;

    public FutureFormula(Formula phi, FormulaInterval interval) {
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
        return "F_" + interval.toString() + "( " + getSubformula(0).toString() + " )";
    }

    public FormulaType getType() {
        return FormulaType.FUTURE;
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
            return false; //same type and subformulae
        }
        if (!(obj instanceof TemporalFormula)) {
            return false; //is also temporal formula
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
