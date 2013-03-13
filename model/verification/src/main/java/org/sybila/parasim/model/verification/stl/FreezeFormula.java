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