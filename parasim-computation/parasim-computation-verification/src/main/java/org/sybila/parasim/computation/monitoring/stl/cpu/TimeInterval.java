package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.verification.stl.FormulaInterval;
import org.sybila.parasim.model.verification.stl.IntervalType;

/**
 * Represents an time interval and enables basic operations with it's bounds.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public class TimeInterval implements FormulaInterval
{
    private float lower,upper;
    private IntervalType type;
    
    public TimeInterval(float lower, float upper, IntervalType type)
    {
        if (lower < 0)
        {
            throw new IllegalArgumentException("Parameter lower must be >= 0.");
        }
        if (lower >= upper)
        {
            throw new IllegalArgumentException("Parameter upper must greater or equal to parameter lower.");
        }
        this.lower = lower;
        this.upper = upper;
        this.type = type;
    }

    @Override
    public float getLowerBound()
    {
        return lower;
    }

    @Override
    public float getUpperBound() {
        return upper;
    }

    @Override
    public IntervalType getIntervalType()
    {
        return type;
    }

    public IntervalType getLowerType()
    {
        if (type == IntervalType.CLOSED || type == IntervalType.RIGHTOPEN)
        {
            return IntervalType.CLOSED;
        }
        else
        {
            return IntervalType.OPEN;
        }
    }

    public IntervalType getUpperType()
    {
        if (type == IntervalType.CLOSED || type == IntervalType.LEFTOPEN)
        {
            return IntervalType.CLOSED;
        }
        else
        {
            return IntervalType.OPEN;
        }
    }

    /**
     * Returns the value of the comparison <b>a</b> OP <code>getLowerBound</code>
     * where OP is either "&lt;=" if the left bound is tight and "&lt;" if it is open.
     * @param a Value to compare with lower bound
     * @return Result of comparison <b>a</b> OP <code>getLowerBound</code>.
     */
    public boolean compareToLower(float a)
    {
        if (type == IntervalType.CLOSED || type == IntervalType.RIGHTOPEN)
        {
            return a <= lower;
        }
        else
        {
            return a < lower;
        }
    }

    /**
     * Returns the value of the comparison <code>getLowerBound</code> OP <b>a</b>
     * where OP is either "&lt;=" if the right bound is tight and "&lt;" if it is open.
     * @param a Value to compare with upper bound
     * @return Result of comparison <code>getUpperBound</code> OP <b>a</b>.
     */
    public boolean compareToUpper(float a)
    {
        if (type == IntervalType.CLOSED || type == IntervalType.LEFTOPEN)
        {
            return upper <= a;
        }
        else
        {
            return upper < a;
        }
    }

}
