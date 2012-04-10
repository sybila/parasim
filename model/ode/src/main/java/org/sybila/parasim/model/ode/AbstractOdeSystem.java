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
package org.sybila.parasim.model.ode;

import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractOdeSystem implements OdeSystem {

    public float value(Point point, int dimension) {
        if (dimension < 0 || dimension >= dimension()) {
            throw new IndexOutOfBoundsException("The dimension is out of the range [0, " + (dimension() - 1) + "].");
        }
        float result = 0;
        for(int c = 0; c < encoding().countCoefficients(dimension); c++) {
            float subResult = encoding().coefficient(dimension, c);
            for(int f = 0; f < encoding().countFactors(dimension, c); f++) {
                subResult *= point.getValue(encoding().factor(dimension, c, f));
            }
            result += subResult;
        }
        return result;
    }

    public float value(float[] point, int dimension) {
        if (dimension < 0 || dimension >= dimension()) {
            throw new IndexOutOfBoundsException("The dimension is out of the range [0, " + (dimension() - 1) + "].");
        }
        float result = 0;
        for(int c = 0; c < encoding().countCoefficients(dimension); c++) {
            float subResult = encoding().coefficient(dimension, c);
            for(int f = 0; f < encoding().countFactors(dimension, c); f++) {
                subResult *= point[encoding().factor(dimension, c, f)];
            }
            result += subResult;
        }
        return result;
    }
}