/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.model.space;

import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OrthogonalSpaceImpl implements OrthogonalSpace {

    private final Point maxBounds;
    private final Point minBounds;
    private final Point size;
    private final OdeSystem odeSystem;

    public OrthogonalSpaceImpl(Point minBounds, Point maxBounds, OdeSystem odeSystem) {
        if (minBounds == null) {
            throw new IllegalArgumentException("The parameter [minBounds] is null.");
        }
        if (maxBounds == null) {
            throw new IllegalArgumentException("The parameter [maxBounds] is null.");
        }
        if (odeSystem == null) {
            throw new IllegalArgumentException("The parameter [odeSystem] is null.");
        }
        if (minBounds.getDimension() != maxBounds.getDimension()) {
            throw new IllegalArgumentException("The dimension of [minBounds] and [maxBounds] doesn't match.");
        }
        float[] sizeData = new float[minBounds.getDimension()];
        for (int i = 0; i < minBounds.getDimension(); i++) {
            if (minBounds.getValue(i) > maxBounds.getValue(i)) {
                throw new IllegalArgumentException("The min bound " + minBounds + " in dimension <" + i + "> is greater than max bound " + maxBounds + ".");
            }
            sizeData[i] = Math.abs(minBounds.getValue(i) - maxBounds.getValue(i));
        }
        this.minBounds = minBounds;
        this.maxBounds = maxBounds;
        this.odeSystem = odeSystem;
        this.size = new ArrayPoint(Math.abs(minBounds.getTime() - maxBounds.getTime()), sizeData);
    }

    @Override
    public int getDimension() {
        return maxBounds.getDimension();
    }

    @Override
    public Point getMaxBounds() {
        return maxBounds;
    }

    @Override
    public Point getMinBounds() {
        return minBounds;
    }

    @Override
    public Point getSize() {
        return size;
    }

    @Override
    public OdeSystem getOdeSystem() {
        return odeSystem;
    }

    @Override
    public float getSize(int dimension) {
        return maxBounds.getValue(dimension) - minBounds.getValue(dimension);
    }

    @Override
    public boolean isIn(Point point) {
        if (point.getDimension() != getDimension()) {
            throw new IllegalArgumentException("The given point has different dimension than the space.");
        }
        for (int dim = 0; dim < getDimension(); dim++) {
            if (point.getValue(dim) < minBounds.getValue(dim)) {
                return false;
            }
            if (point.getValue(dim) > maxBounds.getValue(dim)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isIn(float[] point) {
        if (point.length != getDimension()) {
            throw new IllegalArgumentException("The given point has different dimension than the space.");
        }
        for (int dim = 0; dim < getDimension(); dim++) {
            if (point[dim] < minBounds.getValue(dim)) {
                return false;
            }
            if (point[dim] > maxBounds.getValue(dim)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Element toXML(Document doc) {
        Element space = doc.createElement(OrthogonalSpaceFactory.SPACE_NAME);
        Element time = doc.createElement(OrthogonalSpaceFactory.TIME_NAME);
        time.setAttribute(OrthogonalSpaceFactory.ATTRIBUTE_MIN, Float.toString(minBounds.getTime()));
        time.setAttribute(OrthogonalSpaceFactory.ATTRIBUTE_MAX, Float.toString(maxBounds.getTime()));
        space.appendChild(time);
        for (int dim = 0; dim < getDimension(); dim++) {
            Element dimension;
            if (odeSystem.isVariable(dim)) {
                dimension = doc.createElement(OrthogonalSpaceFactory.VARIABLE_NAME);
                dimension.setAttribute(OrthogonalSpaceFactory.ATTRIBUTE_NAME, odeSystem.getVariable(dim).getName());
                // if the dimension can be resolved through the ODE system, don't write it
                if (minBounds.getValue(dim) == maxBounds.getValue(dim) && odeSystem.getInitialVariableValue(odeSystem.getVariable(dim)) != null && ((Constant)odeSystem.getInitialVariableValue(odeSystem.getVariable(dim)).getSubstitution()).getValue() == minBounds.getValue(dim)) {
                    continue;
                }
            } else {
                dimension= doc.createElement(OrthogonalSpaceFactory.PARAMETER_NAME);
                dimension.setAttribute(OrthogonalSpaceFactory.ATTRIBUTE_NAME, odeSystem.getParameter(dim).getName());
            }
            dimension.setAttribute(OrthogonalSpaceFactory.ATTRIBUTE_MIN, Float.toString(minBounds.getValue(dim)));
            dimension.setAttribute(OrthogonalSpaceFactory.ATTRIBUTE_MAX, Float.toString(maxBounds.getValue(dim)));
            space.appendChild(dimension);
        }
        return space;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof OrthogonalSpaceImpl)) {
            return false;
        }
        OrthogonalSpaceImpl target = (OrthogonalSpaceImpl) obj;
        return (maxBounds.equals(target.maxBounds) && minBounds.equals(target.minBounds));
    }

    @Override
    public int hashCode() {
        final int prime = 41;
        return maxBounds.hashCode() * prime + minBounds.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("(");
        result.append(getDimension());
        result.append(" D ");
        result.append(", T [").append(getMinBounds().getTime()).append(", ").append(getMaxBounds().getTime()).append("]");
        for (int i = 0; i < getDimension() - 1; i++) {
            appendDimension(result, i);
            result.append(", ");
        }
        appendDimension(result, getDimension()-1);
        result.append(")");
        return result.toString();
    }

    private void appendDimension(StringBuilder target, int dim) {
        target.append("[");
        target.append(getMinBounds().getValue(dim));
        target.append(", ");
        target.append(getMaxBounds().getValue(dim));
        target.append("]");
    }
}
