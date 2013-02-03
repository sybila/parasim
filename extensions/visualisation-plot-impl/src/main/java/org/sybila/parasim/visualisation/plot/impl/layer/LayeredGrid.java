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

import org.sybila.parasim.util.Grid;
import org.sybila.parasim.util.Coordinate;
import org.sybila.parasim.util.Block;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class LayeredGrid<T> extends Grid<T> {

    private Block<Layer> layers;

    public LayeredGrid(Block<Layer> layers) {
        super(layers.getDimensions());
        this.layers = layers;
    }

    public Block<Layer> getLayers() {
        return layers;
    }

    public Point getPoint(Coordinate coord) {
        checkDimension(coord);
        int dim = coord.getDimension();
        float[] point = new float[dim];
        for (int i = 0; i < dim; i++) {
            point[i] = layers.get(i, coord.getCoordinate(i)).getValue();
        }
        return new ArrayPoint(0, point, 0, dim);
    }

    public float getLayerValue(int dimension, int index) {
        return getLayers().get(dimension, index).getValue();
    }

    public Coordinate getCoordinate(Point point) {
        Coordinate.Builder build = new Coordinate.Builder(getDimension());
        for (int i = 0; i < getDimension(); i++) {
            build.setCoordinate(i, getCoordinate(point.getValue(i), i, 0, getLayers().getSize(i)));
        }
        return build.create();
    }

    private int getCoordinate(float value, int dim, int begin, int end) {
        if (begin == end) {
            return begin;
        }
        int middle = (begin + end) / 2;
        Layer midLayer = getLayers().get(dim, middle);
        if (midLayer.isIn(value)) {
            return middle;
        }
        if (value < midLayer.getValue()) {
            return getCoordinate(value, dim, begin, middle);
        } else {
            return getCoordinate(value, dim, middle, end);
        }
    }
}
