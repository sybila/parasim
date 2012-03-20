package org.sybila.parasim.model.verification.stl;

import org.sybila.parasim.model.xml.XMLRepresentable;

/**
 * Time interval used in Until, Future and Globaly operators of STL.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface FormulaInterval extends XMLRepresentable
{

    /**
     * Returns the lower bound of the time interval.
     * Must be none-negative.
     *
     * @return Lower bound of time interval.
     */
    float getLowerBound();

    /**
     * Returns the upper bound of the time interval.
     * Must be larger or equal to lower bound. May be positive infinity.
     *
     * @return Upper bound of time interval.
     */
    float getUpperBound();

    /**
     * Returns the type of the interval.
     * @return Type of interval.
     */
    IntervalBoundaryType getLowerBoundaryType();

    /**
     * Returns the type of the interval.
     * @return Type of interval.
     */
    IntervalBoundaryType getUpperBoundaryType();
}
