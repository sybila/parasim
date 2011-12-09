package org.sybila.parasim.model.verification.stl;

/**
 * A simple abstract class representing a general binary operator.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public abstract class BinaryFormula implements Formula
{
    private Formula[] subFormulas;

    public BinaryFormula(Formula phi1, Formula phi2)
    {
        if (phi1 == null)
        {
            throw new IllegalArgumentException("Parameter phi1 is null.");
        }
        if (phi2 == null)
        {
            throw new IllegalArgumentException("Parameter phi2 is null.");
        }
        subFormulas = new Formula[]{phi1, phi2};
    }

    @Override
    public int getArity()
    {
        return 2;
    }

    @Override
    public abstract String toString();

    @Override
    public Formula getSubformula(int index)
    {
        if (index < 0 || index > 1)
        {
            throw new IllegalArgumentException("Index must be 0 or 1.");
        }
        return subFormulas[index];
    }

    @Override
    public boolean equals(Formula formula)
    {
        if (formula.getType() != this.getType()) return false;
        return subFormulas[0].equals(formula.getSubformula(0)) &&
               subFormulas[1].equals(formula.getSubformula(1));
    }

}
