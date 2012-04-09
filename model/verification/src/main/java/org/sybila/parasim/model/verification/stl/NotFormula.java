package org.sybila.parasim.model.verification.stl;

/**
 * Negation of a formula.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class NotFormula extends UnaryFormula {

    public NotFormula(Formula phi) {
        super(phi);
    }

    @Override
    public float getTimeNeeded() {
        return getSubformula(0).getTimeNeeded();
    }

    @Override
    public String toString() {
        return "!( " + getSubformula(0).toString() + " )";
    }

    @Override
    public FormulaType getType() {
        return FormulaType.NOT;
    }
}
