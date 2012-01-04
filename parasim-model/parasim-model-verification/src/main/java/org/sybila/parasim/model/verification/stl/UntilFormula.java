package org.sybila.parasim.model.verification.stl;

/**
 * Until operator. 
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class UntilFormula extends BinaryFormula implements FormulaInterval
{
    private float a,b;
    private IntervalType intervalType;
    
    public UntilFormula(Formula phi1, Formula phi2, float a, float b, IntervalType type)
    {
        super(phi1, phi2);
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
        return java.lang.Math.max(getSubformula(0).getTimeNeeded(),
                                  getSubformula(1).getTimeNeeded()) + b;
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
        return "( "+getSubformula(0).toString()+" ) U ( "+getSubformula(1).toString()+" )";
    }

    @Override
    public FormulaType getType()
    {
        return FormulaType.UNTIL;
    }

    public IntervalType getIntervalType()
    {
        return intervalType;
    }

}
