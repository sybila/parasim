package org.sybila.parasim.model.verification.stl;

/**
 * A simple abstract class representing a general unary operator.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public abstract class UnaryFormula implements Formula
{
    private Formula subFormula;

    public UnaryFormula(Formula phi)
    {
        if (phi == null)
        {
            throw new IllegalArgumentException("Parameter phi is null.");
        }        
        subFormula = phi;
    }

    @Override
    public int getArity()
    {
        return 1;
    }

    @Override
    public abstract String toString();

    @Override
    public Formula getSubformula(int index)
    {
        if (index != 0)
        {
            throw new IllegalArgumentException("Index must be 0.");
        }
        return subFormula;
    }

    @Override
    public boolean equals(Formula formula)
    {
        if (formula.getType() != this.getType()) return false;
        return subFormula.equals(formula.getSubformula(0));
    }

}
