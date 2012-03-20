package org.sybila.parasim.model.verification.stl;

/**
 * A simple abstract class representing a general unary operator.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class UnaryFormula extends AbstractFormula
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
    public Formula getSubformula(int index)
    {
        if (index != 0)
        {
            throw new IllegalArgumentException("Index must be 0.");
        }
        return subFormula;
    }

}
