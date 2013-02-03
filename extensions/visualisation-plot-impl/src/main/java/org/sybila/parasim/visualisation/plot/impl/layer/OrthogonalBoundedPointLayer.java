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
package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class OrthogonalBoundedPointLayer implements Point2DLayer {

    private OrthogonalSpace space;

    public OrthogonalBoundedPointLayer(OrthogonalSpace bounds) {
        space = bounds;
    }

    protected OrthogonalSpace getBounds() {
        return space;
    }

    protected abstract int getXAxis();

    protected abstract int getYAxis();

    @Override
    public float minX() {
        return space.getMinBounds().getValue(getXAxis());
    }

    @Override
    public float maxX() {
        return space.getMaxBounds().getValue(getXAxis());
    }

    @Override
    public float minY() {
        return space.getMinBounds().getValue(getYAxis());
    }

    @Override
    public float maxY() {
        return space.getMaxBounds().getValue(getYAxis());
    }
}
