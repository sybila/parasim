/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.verification;

import java.io.Serializable;

/**
 * Represents some way of specifying a property of an OdeSystem.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface Property extends Serializable {

    /**
     * Formulas such as Until, Future and Globally have as a parameter an
     * interval. To evaluate the satisfaction of a formula on a trajectory a
     * minimal length is needed which can be computed from the structure of the
     * formula.
     *
     * This method returns the time needed to evaluate this formula and all its
     * subformulas. If a formula is evaluated in a single time point 0 will be
     * returned.
     *
     * @return Time or trajectory length needed to evaluate formula.
     */
    float getTimeNeeded();
}
