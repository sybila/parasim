/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.verification.stl;

import java.io.Serializable;
import org.sybila.parasim.model.xml.XMLRepresentable;

/**
 * Time interval used in Until, Future and Globaly operators of STL.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface FormulaInterval extends XMLRepresentable, Serializable {

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
