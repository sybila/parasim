package org.sybila.parasim.model.verification.stl;

/**
 * Until operator. 
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class UntilFormula extends BinaryFormula implements TemporalFormula
{
    private FormulaInterval interval;
    
    public UntilFormula(Formula phi1, Formula phi2, FormulaInterval interval)
    {
        super(phi1, phi2);
        if (interval == null)
        {
            throw new IllegalArgumentException("Parameter interval is null.");
        }
        this.interval = interval;
    }

    @Override
    public float getTimeNeeded()
    {
        return java.lang.Math.max(getSubformula(0).getTimeNeeded(),
                                  getSubformula(1).getTimeNeeded()) + interval.getUpperBound();
    }

    @Override
    public FormulaInterval getInterval()
    {
        return interval;
    }

    @Override
    public String toString()
    {
        return "( "+getSubformula(0).toString()+" ) U_"+interval.toString()+" ( "+getSubformula(1).toString()+" )";
    }

    @Override
    public FormulaType getType()
    {
        return FormulaType.UNTIL;
    }

    @Override
    public boolean equals(Formula formula)
    {
        if (!super.equals(formula)) return false;
        return interval.equals(((TemporalFormula)formula).getInterval());
    }

}
