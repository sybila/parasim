/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
 * Defines Euclidean metric between two points.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EuclideanMetric implements PointDistanceMetric<Distance> {

    public Distance distance(Point first, Point second) {
        if (first == null) {
            throw new IllegalArgumentException("The first point is null.");
        }
        if (second == null) {
            throw new IllegalArgumentException("The second point is null.");
        }
        if (first.getDimension() != second.getDimension()) {
            throw new IllegalArgumentException("Dimensions of two points should be equal.");
        }
        double sqrDist = 0;
        for (int i = 0; i < first.getDimension(); i++) {
            sqrDist += Math.pow(first.getValue(i) - second.getValue(i), 2);
        }
        return new SimpleDistance((float) Math.sqrt(sqrDist));
    }

    public Distance distance(float[] first, float[] second) {
        if (first == null) {
            throw new IllegalArgumentException("The first point is null.");
        }
        if (second == null) {
            throw new IllegalArgumentException("The second point is null.");
        }
        if (first.length != second.length) {
            throw new IllegalArgumentException("Dimensions of two points should be equal.");
        }
        double sqrDist = 0;
        for (int i = 0; i < first.length; i++) {
            sqrDist += Math.pow(first[i] - second[i], 2);
        }
        return new SimpleDistance((float) Math.sqrt(sqrDist));
    }
}
