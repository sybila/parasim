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

import java.util.HashSet;
import java.util.Set;
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
    private final Property property;
    private final Signal[] signals;
    private Robustness inverted;
    private static final PointDistanceMetric<Distance> EUCLIDEAN_METRIC = new EuclideanMetric();

    public SimpleRobustness(float value) {
        this(value, 0, null);
    }

    public SimpleRobustness(float value, float time) {
        this(value, time, null);
    }

    public SimpleRobustness(float value, float time, Property property) {
        this(value, time, property, null);
    }

    private SimpleRobustness(float value, float time, Property property, Robustness inverted) {
        this.value = value;
        this.inverted = inverted;
        this.time = time;
        this.property = property;
        if (property != null) {
            Set<Signal> ss = new HashSet<>();
            for (Signal s: property.getSignals()) {
                ss.add(s);
            }
            signals = ss.toArray(new Signal[ss.size()]);
        } else {
            signals = null;
        }
    }

    @Override
    public float getTime() {
        return time;
    }

    @Override
    public float getValue() {
        return this.value;
    }

    @Override
    public Robustness invert() {
        if (value == 0) {
            return this;
        }
        if (inverted == null) {
            inverted = new SimpleRobustness(-value, time, property, this);
        }
        return inverted;
    }

    @Override
    public LimitedDistance distance(float[] first, float[] second) {
        Distance distance = null;
        if (signals == null) {
            distance = EUCLIDEAN_METRIC.distance(first, second);
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
            for (Signal signal: signals) {
                sqrDist += Math.pow(signal.getValue(first) - signal.getValue(second), 2);
            }
            distance = new SimpleDistance((float) Math.sqrt(sqrDist));
        }
        final Distance finalDistance = distance;
        return new LimitedDistance() {
            @Override
            public boolean isValid() {
                return finalDistance.value() < Math.abs(value);
            }
            @Override
            public float value() {
                return finalDistance.value();
            }
        };
    }

    @Override
    public LimitedDistance distance(Point first, Point second) {
        Distance distance;
        if (signals == null) {
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
            for (Signal signal: signals) {
                sqrDist += Math.pow(signal.getValue(first) - signal.getValue(second), 2);
            }
            distance = new SimpleDistance((float) Math.sqrt(sqrDist));
        }
        final Distance finalDistance = distance;
        return new LimitedDistance() {
            @Override
            public boolean isValid() {
                return finalDistance.value() < Math.abs(value);
            }
            @Override
            public float value() {
                return finalDistance.value();
            }
        };
    }

    @Override
    public String toString() {
        return Float.toString(value);
    }
}