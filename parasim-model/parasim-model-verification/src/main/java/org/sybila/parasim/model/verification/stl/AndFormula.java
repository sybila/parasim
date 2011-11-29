package org.sybila.parasim.model.verification.stl;

/**
 * The conjunction of two formulas.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public class AndFormula extends BinaryFormula
{   

    public AndFormula(Formula phi1, Formula phi2)
    {
        super(phi1, phi2);
    }

    @Override
    public float getTimeNeeded()
    {
        return java.lang.Math.max(getSubformula(0).getTimeNeeded(),
                                  getSubformula(1).getTimeNeeded());
    }

    @Override
    public String toString()
    {
        return "( "+getSubformula(0).toString()+" ) && ( "+getSubformula(1).toString()+" )";
    }

    @Override
    public FormulaType getType()
    {
        return FormulaType.AND;
    }

}
