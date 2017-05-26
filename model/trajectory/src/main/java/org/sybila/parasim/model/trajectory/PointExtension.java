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
package org.sybila.parasim.model.trajectory;

/**
 * Supplements a Point by giving it an unique position number on it's trajectory.
 * A point's position on a trajectory must not change even if the
 * trajectory is prolonged or truncated (from begining or end).
 *
 * This means that as long as the trajectory T contains the given point P
 * calling T.getPoint(P.getPoisitionOnTrajectory()) must return P.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PointExtension extends Point {

    /**
     * Returns the position of this point on it's trajectory as an integer.
     * The first point on a untruncated trajectory has index 0.
     *
     * @return Unique position on trajectory.
     */
    int getPositionOnTrajectory();

    /**
     * Copies the dimension values to specified array.
     * @param dest Destination where to copy dimension values.
     * @param destPos Position inside destination where to copy values.
     */
    void arrayCopy(float[] dest, int destPos);
}
