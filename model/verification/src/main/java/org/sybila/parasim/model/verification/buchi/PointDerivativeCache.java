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
package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.ode.OdeSystem;
import java.util.Iterator;

/**
 * Enables to retrieve derivatives of the OdeSystem in the given point.
 * Computations are cached for further use so each dimension is computed at
 * most once.
 *
 * @author Sven Drazan <sven@mail.muni.cz>
 */
public class PointDerivativeCache implements PointDerivative {

    private Point p;
    private OdeSystem ode;
    private float[] derivatives;
    private boolean[] computed;

    public PointDerivativeCache(Point p, OdeSystem ode) {
        this.p = p;
        this.ode = ode;
        computed = new boolean[p.getDimension()];
        derivatives = new float[p.getDimension()];
    }

    /**
     * Returns the OdeSystems derivative of variable varIndex in this point.
     * @param varIndex Index of the variable who's derivative to return.
     * @return Value of the derivative.
     */
    public float getDerivative(int varIndex) {
        if (varIndex < 0 || varIndex >= p.getDimension()) {
            throw new IllegalArgumentException("The index is out of the range [0, " + (p.getDimension() - 1) + "]");
        }
        if (!computed[varIndex]) {
            derivatives[varIndex] = ode.value(p, varIndex);
            computed[varIndex] = true;
        }
        return derivatives[varIndex];
    }

    /**
     * @return Number of dimensions of given point.
     */
    public int getDimension() {
        return p.getDimension();
    }

    /**
     * @return time of the point
     */
    public float getTime() {
        return p.getTime();
    }

    /**
     * @param index The dimension of who's value to return.
     * @return Value of given dimension.
     */
    public float getValue(int index) {
        return p.getValue(index);
    }

    /**
     * @return Values of all dimensions as an array without time
     */
    public float[] toArray() {
        return p.toArray();
    }

    @Override
    public Iterator<Float> iterator() {
        return null; /* I think that points should not be iterable, only trajectories! */
    }

    public float[] toArrayCopy() {
        return p.toArrayCopy();
    }
}
