package org.sybila.parasim.model.verification.stl;

/**
 * Future or Finally F operator.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class FutureFormula extends UnaryFormula implements FormulaInterval
{
    private float a,b;
    private IntervalType intervalType;
    
    public FutureFormula(Formula phi, float a, float b, IntervalType type)
    {
        super(phi);
        if (a < 0)
        {
            throw new IllegalArgumentException("Parameter a must be non negative.");
        }
        if (b < a)
        {
            throw new IllegalArgumentException("Parameter b must be larger than a.");
        }
        this.a = a;
        this.b = b;
        this.intervalType = type;
    }

    @Override
    public float getTimeNeeded()
    {
        return getSubformula(0).getTimeNeeded() + b;
    }

    @Override
    public float getLowerBound()
    {
        return a;
    }

    @Override
    public float getUpperBound()
    {
        return b;
    }

    @Override
    public String toString()
    {
        return "F( "+getSubformula(0).toString()+" )";
    }

    public FormulaType getType()
    {
        return FormulaType.FUTURE;
    }

    @Override
    public boolean equals(Formula formula)
    {
        if (!super.equals(formula)) return false;
        if ( ((FutureFormula)formula).getLowerBound() != this.getLowerBound()) return false;
        if ( ((FutureFormula)formula).getUpperBound() != this.getUpperBound()) return false;
        return true;
    }

    public IntervalType getIntervalType()
    {
        return intervalType;
    }
    
}
