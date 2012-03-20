package org.sybila.parasim.model.verification.stl;

import org.sybila.parasim.model.trajectory.Point;

/**
 * A general predicate evaluable in a Point of a Trajectory.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class Predicate extends AbstractFormula
{
    /**
     * Returns the boolean validity of this predicate in given point.
     *
     * @param p Point in which to validate this predicate.
     * @return Boolean validity of predicate in given point.
     */
    public abstract boolean getValidity(Point p);

    /**
     * Returns the qualitative value of this predicate in given point.
     *
     * @param p Point in which to evaluate this predicate.
     * @return Qualitative value of predicate in given point.
     */
    public abstract float getValue(Point p);

    @Override
    public int getArity()
    {
        return 0;
    }

    @Override
    public Formula getSubformula(int index)
    {
        throw new IllegalArgumentException("Predicate has no subformulas.");
    }

    @Override
    public float getTimeNeeded()
    {
        return 0;
    }

    @Override
    public abstract String toString();

    @Override
    public FormulaType getType()
    {
        return FormulaType.PREDICATE;
    }
}
