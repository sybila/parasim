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
package org.sybila.parasim.model.verification;

import java.util.Collection;
import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.trajectory.EuclideanMetric;
import org.sybila.parasim.model.trajectory.LimitedDistance;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointDistanceMetric;
import org.sybila.parasim.model.trajectory.SimpleDistance;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class SimpleRobustness implements Robustness {

    private final float time;
    private final float value;
    private final Collection<Integer> considiredDimensions;
    private Robustness inverted;
    private static final PointDistanceMetric<Distance> EUCLIDEAN_METRIC = new EuclideanMetric();

    public SimpleRobustness(float value) {
        this(value, 0, null);
    }

    public SimpleRobustness(float value, float time) {
        this(value, time, null);
    }

    public SimpleRobustness(float value, float time, Collection<Integer> consideredDimensions) {
        this(value, time, consideredDimensions, null);
    }

    private SimpleRobustness(float value, float time, Collection<Integer> consideredDimensions, Robustness inverted) {
        this.value = value;
        this.inverted = inverted;
        this.time = time;
        this.considiredDimensions = consideredDimensions;
    }

    public float getTime() {
        return time;
    }

    public float getValue() {
        return this.value;
    }

    public Robustness invert() {
        if (value == 0) {
            return this;
        }
        if (inverted == null) {
            inverted = new SimpleRobustness(-value, time, considiredDimensions, this);
        }
        return inverted;
    }

    @Override
    public LimitedDistance distance(float[] first, float[] second) {
        Distance distance = null;
        if (considiredDimensions == null) {
            EUCLIDEAN_METRIC.distance(first, second);
        } else {
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
            for (int dim: considiredDimensions) {
                sqrDist += Math.pow(first[dim] - second[dim], 2);
            }
            distance = new SimpleDistance((float) Math.sqrt(sqrDist));
        }
        final Distance finalDistance = distance;
        return new LimitedDistance() {
            public boolean isValid() {
                return finalDistance.value() <= Math.abs(value);
            }
            public float value() {
                return finalDistance.value();
            }
        };
    }

    @Override
    public LimitedDistance distance(Point first, Point second) {
        Distance distance = null;
        if (considiredDimensions == null) {
            distance = EUCLIDEAN_METRIC.distance(first, second);
        } else {
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
            for (int dim: considiredDimensions) {
                sqrDist += Math.pow(first.getValue(dim) - second.getValue(dim), 2);
            }
            distance = new SimpleDistance((float) Math.sqrt(sqrDist));
        }
        final Distance finalDistance = distance;
        return new LimitedDistance() {
            public boolean isValid() {
                return finalDistance.value() <= Math.abs(value);
            }
            public float value() {
                return finalDistance.value();
            }
        };
    }
}