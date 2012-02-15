package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.computation.monitoring.PropertyRobustness;

/**
 * A simple property robustness object.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public class SimplePropertyRobustness implements PropertyRobustness
{
    private float time;
    private float value;
    private float derivative;

    public SimplePropertyRobustness(float time, float value, float derivative)
    {
        if (time < 0 || time == Float.NaN ||
            time == Float.NEGATIVE_INFINITY ||
            time == Float.POSITIVE_INFINITY)
        {
            throw new IllegalArgumentException("Parameter time must be >= 0 and finite.");
        }
        if (value == Float.NaN ||
            value == Float.NEGATIVE_INFINITY ||
            value == Float.POSITIVE_INFINITY)
        {
            throw new IllegalArgumentException("Parameter value must be a finite number.");
        }
        if (derivative == Float.NaN ||
            derivative == Float.NEGATIVE_INFINITY ||
            derivative == Float.POSITIVE_INFINITY)
        {
            throw new IllegalArgumentException("Parameter derivative must be a finite number.");
        }
        this.time = time;
        this.value = value;
        this.derivative = derivative;
    }

    public SimplePropertyRobustness(PropertyRobustness pr)
    {
        this(pr.getTime(), pr.value(), pr.getValueDerivative());
    }

    @Override
    public float getTime()
    {
        return time;
    }

    @Override
    public float getValueDerivative()
    {
        return derivative;
    }

    @Override
    public float value()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "[" + getTime() + ", " + value() + ", " + getValueDerivative() + "]";
    }

    /**
     * Compares the two given property robustnesses.
     *
     * @param pr1 First property robustness
     * @param pr2 Second property robustness
     * @return -1 if pr1 < pr2, 0 if pr1 == pr2 and 1 if pr1 > pr2
     */
    int compare(PropertyRobustness pr1, PropertyRobustness pr2)
    {
        if (pr1.value() < pr2.value()) return -1;
        if (pr1.value() > pr2.value()) return 1;
        if (pr1.getValueDerivative() < pr2.getValueDerivative() ) return -1;
        if (pr1.getValueDerivative() > pr2.getValueDerivative() ) return 1;
        return 0;
    }
}
