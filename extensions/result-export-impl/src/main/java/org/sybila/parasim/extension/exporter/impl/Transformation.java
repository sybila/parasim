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
package org.sybila.parasim.extension.exporter.impl;

import org.sybila.parasim.model.space.OrthogonalSpace;

public class Transformation {

    private final OrthogonalSpace space;
    private final int size;

    public Transformation(OrthogonalSpace space, int size) {
        this.space = space;
        this.size = size;
    }

    public int transform(int dimension, float coord) {
        double unit = Math.floor(size / (space.getMaxBounds().getValue(dimension) - space.getMinBounds().getValue(dimension)));
        double moved = coord - space.getMinBounds().getValue(dimension);
        return (int) Math.round(unit * moved);
    }

}
