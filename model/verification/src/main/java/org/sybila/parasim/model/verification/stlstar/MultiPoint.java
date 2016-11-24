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
package org.sybila.parasim.model.verification.stlstar;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Structure containing multiple points -- used to contain values for given time
 * and for all frozen times. Individual cannot be null.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface MultiPoint {

    /**
     * Returns the number of point contained in this structure.
     *
     * @return Number of points.
     */
    int getDimension();

    /**
     * Returns single point from this structure. Should throw an exception when
     * index is out of bounds.
     *
     * @param index Index of the point.
     * @return Point of given index.
     */
    Point getPoint(int index);
}
