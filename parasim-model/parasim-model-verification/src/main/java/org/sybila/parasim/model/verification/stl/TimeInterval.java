package org.sybila.parasim.model.verification.stl;

/**
 * Represents an time interval and enables basic operations with it's bounds.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class TimeInterval implements FormulaInterval
{
    private float lower,upper;
    private IntervalBoundaryType lowerType, upperType;
    
    public TimeInterval(float lower, float upper, IntervalBoundaryType lowerType, IntervalBoundaryType upperType)
    {
        if (lower < 0)
        {
            throw new IllegalArgumentException("Parameter lower must be >= 0.");
        }
        if (lower >= upper)
        {
            throw new IllegalArgumentException("Parameter upper must greater or equal to parameter lower.");
        }
        if (lowerType == null)
        {
            throw new IllegalArgumentException("Parameter lowerType is null.");
        }
        if (upperType == null)
        {
            throw new IllegalArgumentException("Parameter upperType is null.");
        }
        this.lower = lower;
        this.upper = upper;
        this.lowerType = lowerType;
        this.upperType = upperType;
    }

    public TimeInterval(float lower, float upper, IntervalBoundaryType type)
    {
        this(lower, upper, type, type);
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
    public IntervalBoundaryType getLowerBoundaryType()
    {
        return lowerType;
    }

    @Override
    public IntervalBoundaryType getUpperBoundaryType()
    {
        return upperType;
    }

    /**
     * Returns the value of the comparison <code>getLowerBound</code> OP <b>a</b>
     * where OP is either "&lt;=" if the left bound is tight and "&lt;" if it is open.
     *
     * @param a Value to compare with lower bound
     * @return Result of comparison <code>getLowerBound</code> OP <b>a</b>.
     */
    public boolean largerThenLower(float a)
    {
        if (lowerType == IntervalBoundaryType.CLOSED)
        {
            return lower <= a;
        }
        else
        {
            return lower < a;
        }
    }

    /**
     * Returns the value of the comparison <b>a</b> OP <code>getUpperBound</code>
     * where OP is either "&lt;=" if the right bound is tight and "&lt;" if it is open.
     * 
     * @param a Value to compare with upper bound
     * @return Result of comparison <b>a</b> OP <code>getUpperBound</code>.
     */
    public boolean smallerThenUpper(float a)
    {
        if (upperType == IntervalBoundaryType.CLOSED)
        {
            return a <= upper;
        }
        else
        {
            return a < upper;
        }
    }

    @Override
    public String toString()
    {
        return (lowerType == IntervalBoundaryType.CLOSED ? "[" : "(") + lower + 
                "," + upper + (upperType == IntervalBoundaryType.CLOSED ? "]" : ")");
    }

    @Override
    public boolean equals(FormulaInterval other)
    {
        return (this.lowerType == other.getLowerBoundaryType()) &&
               (this.upperType == other.getUpperBoundaryType()) &&
               (this.lower == other.getLowerBound()) &&
               (this.upper == other.getUpperBound());

    }

}