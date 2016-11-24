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

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.model.trajectory.Point;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ArrayMultiPoint implements MultiPoint {

    private Point[] points;

    private void initialize(Point[] points) {
        Validate.noNullElements(points);
        if (points.length > 0) {
            int dim = points[0].getDimension();
            for (int i = 1; i < points.length; i++) {
                if (points[i].getDimension() != dim) {
                    throw new IllegalArgumentException("Point have different dimensions.");
                }
            }
        }
        this.points = Arrays.copyOf(points, points.length);
    }

    public ArrayMultiPoint(Point[] points) {
        Validate.notNull(points);
        initialize(points);
    }

    public ArrayMultiPoint(List<Point> points) {
        Validate.notNull(points);
        initialize(points.toArray(new Point[0]));
    }

    @Override
    public int getDimension() {
        return points.length;
    }

    @Override
    public Point getPoint(int index) {
        Validate.validIndex(points, index);
        return points[index];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrayMultiPoint)) {
            return false;
        }
        ArrayMultiPoint target = (ArrayMultiPoint) obj;
        return Arrays.equals(points, target.points);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(points);
    }
}
