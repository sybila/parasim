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
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class PointTrajectory extends AbstractTrajectory implements TrajectoryWithNeighborhood {

    /** First point of the trajectory. */
    private final Point initialPoint;

    public PointTrajectory(Point initialPoint) {
        this(DataBlock.EMPTY_DATABLOCK, initialPoint);
    }

    public PointTrajectory(float time, float... data) {
        this(new ArrayPoint(time, data));
    }

    public PointTrajectory(DataBlock<Trajectory> neighborhood, Point initialPoint) {
        super(neighborhood, initialPoint.getDimension(), 1);
        this.initialPoint = initialPoint;
    }

    public PointTrajectory(DataBlock<Trajectory> neighborhood, float time, float... data) {
        this(neighborhood, new ArrayPoint(time, data));
    }

    @Override
    public Point getPoint(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("The point index is out of the range [0, " + (getLength() - 1) + "]");
        }
        return initialPoint;
    }

    @Override
    public String toString() {
        return "[" + initialPoint + "]";
    }

}