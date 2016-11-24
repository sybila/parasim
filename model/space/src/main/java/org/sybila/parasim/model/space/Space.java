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
package org.sybila.parasim.model.space;

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.xml.XMLRepresentable;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Space extends XMLRepresentable {

    /**
     * @return a space dimension
     */
    int getDimension();

    /**
     * @return ODE system the space is related to
     */
    OdeSystem getOdeSystem();

    /**
     * @param dimension
     * @return size of space in the given dimension
     */
    float getSize(int dimension);

    /**
     * Checks whether the given point is in the space (it doesn't check time).
     * @param point
     * @return
     */
    boolean isIn(Point point);

    /**
     * Checks whether the given point is in the space.
     * @param point
     * @return
     */
    boolean isIn(float[] point);
}
