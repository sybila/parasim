package org.sybila.parasim.model.verification.stl;

/**
 * Structural type of a STL formula.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public enum FormulaType
{
    PREDICATE, AND, OR, NOT, UNTIL, FUTURE, GLOBALY;

    private static final FormulaType[] values = {PREDICATE, AND, OR, NOT, UNTIL, FUTURE, GLOBALY};

    FormulaType fromInt(int value)
    {
        if (value < 0 || value >= values.length)
        {
            throw new IllegalArgumentException("Unknown ordinal value of type.");
        }
        return values[value];
    }
}
