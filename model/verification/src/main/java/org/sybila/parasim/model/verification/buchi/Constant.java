package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Constant is a float number to figure inside atomic propositions.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class Constant implements AtomicPropOperand
{
    private float value;

    public Constant(float value)
    {
        this.value = value;
    }

    public float getValue(Point point)
    {
        return value;
    }

    @Override
    public String toString()
    {
        return Float.toString(value);
    }

}
