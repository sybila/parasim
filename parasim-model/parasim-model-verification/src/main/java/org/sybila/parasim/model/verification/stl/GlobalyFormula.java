package org.sybila.parasim.model.verification.stl;

/**
 * Globaly G operator.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class GlobalyFormula extends UnaryFormula implements FormulaInterval
{
    private float a,b;

    public GlobalyFormula(Formula phi, float a, float b)
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
        return "G( "+getSubformula(0).toString()+" )";
    }

    @Override
    public FormulaType getType()
    {
        return FormulaType.GLOBALY;
    }

    @Override
    public boolean equals(Formula formula)
    {
        if (!super.equals(formula)) return false;
        if ( ((FutureFormula)formula).getLowerBound() != this.getLowerBound()) return false;
        if ( ((FutureFormula)formula).getUpperBound() != this.getUpperBound()) return false;
        return true;
    }
}
