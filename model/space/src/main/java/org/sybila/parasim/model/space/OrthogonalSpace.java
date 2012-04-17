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
package org.sybila.parasim.model.space;

import org.sybila.parasim.model.trajectory.Point;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OrthogonalSpace implements Space {

    private Point maxBounds;
    private Point minBounds;

    public OrthogonalSpace(Point minBounds, Point maxBounds) {
        if (minBounds == null) {
            throw new IllegalArgumentException("The parameter [minBounds] is null.");
        }
        if (maxBounds == null) {
            throw new IllegalArgumentException("The parameter [maxBounds] is null.");
        }
        if (minBounds.getDimension() != maxBounds.getDimension()) {
            throw new IllegalArgumentException("The dimension of [minBounds] and [maxBounds] doesn't match.");
        }
        for (int i = 0; i < minBounds.getDimension(); i++) {
            if (minBounds.getValue(i) > maxBounds.getValue(i)) {
                throw new IllegalArgumentException("The min bound " + minBounds + " in dimension <" + i + "> is greater than max bound " + maxBounds + ".");
            }
        }
        this.minBounds = minBounds;
        this.maxBounds = maxBounds;
    }

    @Override
    public int getDimension() {
        return maxBounds.getDimension();
    }

    public Point getMaxBounds() {
        return maxBounds;
    }

    public Point getMinBounds() {
        return minBounds;
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
        for (int dim = 0; dim < getDimension(); dim++) {
            Element dimension = doc.createElement(OrthogonalSpaceFactory.DIMENSION_NAME);
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
        if (!(obj instanceof OrthogonalSpace)) {
            return false;
        }
        OrthogonalSpace target = (OrthogonalSpace) obj;
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
