package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.ode.OdeSystem;
import java.util.Iterator;

/**
 *
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public class ExtendedPoint implements PointDerivative {

    private Point p;
    private OdeSystem ode;
    private float[] derivatives;
    private boolean[] computed;

    public ExtendedPoint(Point p, OdeSystem ode)
    {
        this.p = p;
        this.ode = ode;
        computed = new boolean[p.getDimension()];
        derivatives = new float[p.getDimension()];
    }

    /**
     * Returns the OdeSystems derivative of variable varIndex in this point.
     * @param varIndex Index of the variable who's derivative to return.
     * @return Value of the derivative.
     */
    public float getDerivative(int varIndex)
    {
        if (varIndex < 0 || varIndex >= p.getDimension())
        {
            throw new IllegalArgumentException("The index is out of the range [0, " + (p.getDimension() - 1) + "]");
        }
        if (!computed[varIndex])
        {
            derivatives[varIndex] = ode.value(p, varIndex);
            computed[varIndex] = true;
        }
        return derivatives[varIndex];
    }

    /**
     * @return Number of dimensions of given point.
     */
    public int getDimension()
    {
      return p.getDimension();
    }

    /**
     * @return time of the point
     */
    public float getTime()
    {
        return p.getTime();
    }

    /**
     * @param index The dimension of who's value to return.
     * @return Value of given dimension.
     */
    public float getValue(int index)
    {
        return p.getValue(index);
    }

    /**
     * @return Values of all dimensions as an array without time
     */
    public float[] toArray()
    {
        return p.toArray();
    }

    @Override
    public Iterator<Float> iterator() {
        return null; /* I think that points should not be iterable, only trajectories! */
    }
}
