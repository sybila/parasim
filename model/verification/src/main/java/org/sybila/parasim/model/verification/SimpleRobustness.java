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

import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.trajectory.EuclideanMetric;
import org.sybila.parasim.model.trajectory.LimitedDistance;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointDistanceMetric;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class SimpleRobustness implements Robustness {

    private final float time;
    private final float value;
    private Robustness inverted;
    private static final PointDistanceMetric<Distance> EUCLIDEAN_METRIC = new EuclideanMetric();

    public SimpleRobustness(float value) {
        this(value, 0, null);
    }

    public SimpleRobustness(float value, float time) {
        this(value, time, null);
    }

    private SimpleRobustness(float value, float time, Robustness inverted) {
        this.value = value;
        this.inverted = inverted;
        this.time = time;
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
            inverted = new SimpleRobustness(-value, time, this);
        }
        return inverted;
    }

    @Override
    public LimitedDistance distance(float[] first, float[] second) {
        final Distance distance = EUCLIDEAN_METRIC.distance(first, second);
        return new LimitedDistance() {
            public boolean isValid() {
                return distance.value() <= Math.abs(value);
            }
            public float value() {
                return distance.value();
            }
        };
    }

    @Override
    public LimitedDistance distance(Point first, Point second) {
        final Distance distance = EUCLIDEAN_METRIC.distance(first, second);
        return new LimitedDistance() {
            public boolean isValid() {
                return distance.value() <= Math.abs(value);
            }
            public float value() {
                return distance.value();
            }
        };
    }
}
