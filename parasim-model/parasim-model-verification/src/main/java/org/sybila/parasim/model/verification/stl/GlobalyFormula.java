package org.sybila.parasim.model.verification.stl;

/**
 * Globaly G operator.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class GlobalyFormula extends UnaryFormula implements TemporalFormula
{
    private FormulaInterval interval;

    public GlobalyFormula(Formula phi, FormulaInterval interval)
    {
        super(phi);
        if (interval == null)
        {
            throw new IllegalArgumentException("Parameter interval is null.");
        }
        this.interval = interval;
    }

    @Override
    public float getTimeNeeded()
    {
        return getSubformula(0).getTimeNeeded() + interval.getUpperBound();
    }

    @Override
    public FormulaInterval getInterval()
    {
        return interval;
    }

    @Override
    public String toString()
    {
        return "G_"+interval.toString()+"( "+getSubformula(0).toString()+" )";
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
        return interval.equals(((TemporalFormula)formula).getInterval());
    }

}
