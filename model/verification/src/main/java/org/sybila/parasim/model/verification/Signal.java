/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.model.verification;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.stlstar.MultiPoint;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Signal {

    /**
     * Returns the qualitative value of this signal in given point.
     *
     * @param point Point in which to evaluate this signal.
     * @return Qualitative value of signal in given point.
     */
    float getValue(Point point);

    /**
     * Returns the qualitative value of this signal in given point.
     *
     * @param point Point in which to evaluate this signal.
     * @return Qualitative value of signal in given point.
     */
    float getValue(float[] point);

    /**
     * Returns the boolean validity of this signal in given point.
     *
     * @param p Point in which to validate this predicate.
     * @return Boolean validity of predicate in given point.
     */
    boolean isValid(Point p);

    /**
     * Returns the boolean validity of this signal in a given multipoint (with
     * frozen signal values).
     *
     * @param mp Multipoint in which to evaluate the signal.
     * @return Qualitative value of signal in given multipoint.
     */
    float getValue(MultiPoint mp);

    /**
     * Returns the boolean validity of this signal in given multipoint.
     *
     * @param mp Multipoint in which to validate this predicate.
     * @return Boolean validity of predicate in given multipoint.
     */
    boolean isValid(MultiPoint mp);
}
