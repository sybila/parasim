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
package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;

/**
 * A simple property robustness object.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public class SimplePropertyRobustness implements PropertyRobustness {

    private float time;
    private float value;
    private float derivative;

    public SimplePropertyRobustness(float time, float value, float derivative) {
        if (time < 0 || time == Float.NaN
                || time == Float.NEGATIVE_INFINITY
                || time == Float.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("Parameter time must be >= 0 and finite.");
        }
        if (value == Float.NaN
                || value == Float.NEGATIVE_INFINITY
                || value == Float.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("Parameter value must be a finite number.");
        }
        if (derivative == Float.NaN
                || derivative == Float.NEGATIVE_INFINITY
                || derivative == Float.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("Parameter derivative must be a finite number.");
        }
        this.time = time;
        this.value = value;
        this.derivative = derivative;
    }

    public SimplePropertyRobustness(PropertyRobustness pr) {
        this(pr.getTime(), pr.value(), pr.getValueDerivative());
    }

    @Override
    public float getTime() {
        return time;
    }

    @Override
    public float getValueDerivative() {
        return derivative;
    }

    @Override
    public float value() {
        return value;
    }

    @Override
    public String toString() {
        return "[" + getTime() + ", " + value() + ", " + getValueDerivative() + "]";
    }

    /**
     * Compares the two given property robustnesses.
     *
     * @param pr1 First property robustness
     * @param pr2 Second property robustness
     * @return -1 if pr1 < pr2, 0 if pr1 == pr2 and 1 if pr1 > pr2
     */
    int compare(PropertyRobustness pr1, PropertyRobustness pr2) {
        if (pr1.value() < pr2.value()) {
            return -1;
        }
        if (pr1.value() > pr2.value()) {
            return 1;
        }
        if (pr1.getValueDerivative() < pr2.getValueDerivative()) {
            return -1;
        }
        if (pr1.getValueDerivative() > pr2.getValueDerivative()) {
            return 1;
        }
        return 0;
    }
}
