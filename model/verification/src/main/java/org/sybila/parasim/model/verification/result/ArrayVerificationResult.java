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
package org.sybila.parasim.model.verification.result;

import org.sybila.parasim.model.trajectory.PointWithNeighborhood;
import org.sybila.parasim.model.verification.Robustness;

/**
 * Stores points and their associated robustness values in an array.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ArrayVerificationResult extends AbstractVerificationResult {
    private final PointWithNeighborhood[] points;
    private final Robustness[] robustness;

    /**
     * Creates new verification result with specified contents. Note: arguments are not copied.
     * @param size Number of points.
     * @param points Array containing points.
     * @param robustness Array containing robustness values.
     * @throws IllegalArgumentException when lengths of given arrays do not match.
     */
    public ArrayVerificationResult(PointWithNeighborhood[] points, Robustness[] robustness) {
        if (robustness.length != points.length) {
            throw new IllegalArgumentException("Lengths of points and robustness arrays don't match.");
        }
        this.points = points;
        this.robustness = robustness;
    }

    public ArrayVerificationResult(PointWithNeighborhood[] points, Robustness[] robustness, Robustness globalRobustness) {
        this(points, robustness);
        setGlobalRobustness(globalRobustness);
    }

    @Override
    public PointWithNeighborhood getPoint(int index) {
        return points[index];
    }

    @Override
    public Robustness getRobustness(int index) {
        return robustness[index];
    }

    @Override
    public int size() {
        return points.length;
    }

    @Override
    public VerificationResult merge(VerificationResult toMerge) {
        if (toMerge == null) {
            throw new IllegalArgumentException("The parameter [toMerge] is null.");
        }
        Robustness[] newRobustnesses = new Robustness[size() + toMerge.size()];
        PointWithNeighborhood[] newPoints = new PointWithNeighborhood[size() + toMerge.size()];
        // copy this data
        System.arraycopy(points, 0, newPoints, 0, points.length);
        System.arraycopy(robustness, 0, newRobustnesses, 0, robustness.length);
        // copy other data
        for (int i=0; i<toMerge.size(); i++) {
            newPoints[size() + i] = toMerge.getPoint(i);
            newRobustnesses[size() + i] = toMerge.getRobustness(i);
        }
        return new ArrayVerificationResult(newPoints, newRobustnesses);
    }
}
