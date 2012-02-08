package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.ode.OdeSystem;

/**
 * Operand is a variable of the given OdeSystem.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public class Variable<P extends Point> implements AtomicPropOperand<P>
{
    private OdeSystem ode;
    private int varIndex;

    public Variable(OdeSystem ode, int varIndex)
    {
        this.ode = ode;
        this.varIndex = varIndex;
    }

    public float getValue(P point)
    {
        return point.getValue(varIndex);
    }

    @Override
    public String toString()
    {
        return ode.getVariable(varIndex).getName();
    }
}
