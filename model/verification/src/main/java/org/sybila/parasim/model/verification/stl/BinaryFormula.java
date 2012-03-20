package org.sybila.parasim.model.verification.stl;

/**
 * A simple abstract class representing a general binary operator.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class BinaryFormula extends AbstractFormula
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
    public Formula getSubformula(int index)
    {
        if (index < 0 || index > 1)
        {
            throw new IllegalArgumentException("Index must be 0 or 1.");
        }
        return subFormulas[index];
    }
}
