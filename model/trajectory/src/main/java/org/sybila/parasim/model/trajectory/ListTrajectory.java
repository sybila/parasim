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

import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ListTrajectory extends AbstractTrajectory {

    private List<Point> points;

    /**
     * Creates a new ListTrajectory with the given points.
     *
     * @param points point sequence
     */
    public ListTrajectory(List<Point> points) {
        super(points.get(0).getDimension(), points.size());
        this.points = points;
    }

    public ListTrajectory(DataBlock<Trajectory> neighborhood, List<Point> points) {
        super(neighborhood, points.get(0).getDimension(), points.size());
        this.points = points;
    }

    @Override
    public Point getPoint(int index) {
        return points.get(index);
    }
}
