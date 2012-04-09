package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.ode.OdeSystem;

/**
 * Operand is a given OdeSystem's variable's derivative.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class VariableDerivative<P extends PointDerivative> implements AtomicPropOperand<P> {

    private OdeSystem ode;
    private int varIndex;

    public VariableDerivative(OdeSystem ode, int varIndex) {
        this.ode = ode;
        this.varIndex = varIndex;
    }

    public float getValue(P point) {
        return point.getDerivative(varIndex);
    }

    @Override
    public String toString() {
        return "d" + ode.getVariable(varIndex).getName();
    }
}
