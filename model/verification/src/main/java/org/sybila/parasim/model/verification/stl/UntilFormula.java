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
