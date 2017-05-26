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
 * Trajectories implementing this interface can be prolonged be using append()
 * and truncated from the end to a specified length using truncate().
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface MutableTrajectory extends Trajectory {

    /**
     * Appends the given trajectory to the end of this trajectory thus increasing
     * it's length.
     *
     * @param trajectory Trajectory to be appended to this one.
     */
    void append(Trajectory trajectory);

    /**
     * Truncates this trajectory to lenght newLength. If newLength is not valid
     * an exception is trown.
     *
     * @param newLength Number of points of this trajectory to preserve
     *        from begining.
     */
    void truncate(int newLength);
}
