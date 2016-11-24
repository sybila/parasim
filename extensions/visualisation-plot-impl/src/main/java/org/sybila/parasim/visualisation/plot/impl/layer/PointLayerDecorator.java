/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

import java.util.Map;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 * Encapsulates {@link LayerMetaFactory} and corresponding {@link LayerFactory}
 * and {@link Point2DLayer} so that they may be decorated in unified manner.
 *
 * Implementation note: contains all three currently valid objects. When a new
 * object should be generated (i.e. by calling {@link LayerMetaFactory#getLayerFactory(int, int)}
 * or {@link LayerFactory#getLayer(java.util.Map)}), contained objects is
 * updated and pointer to this object is returned.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PointLayerDecorator implements LayerMetaFactory, LayerFactory, Point2DLayer {

    private LayerMetaFactory meta;
    private LayerFactory fact;
    private Point2DLayer layer;

    /**
     * Initializes decorator with {@link LayerMetaFactory}.
     */
    public PointLayerDecorator(LayerMetaFactory contents) {
        meta = contents;
    }

    //LayerMetaFactory
    @Override
    public LayerFactory getLayerFactory(int xAxis, int yAxis) {
        fact = meta.getLayerFactory(xAxis, yAxis);
        return this;
    }

    //LayerFactory
    @Override
    public int ticks(int index) {
        return fact.ticks(index);
    }

    @Override
    public int getTicks(int index, float value) {
        return fact.getTicks(index, value);
    }

    @Override
    public float getValue(int index, int ticks) {
        return fact.getValue(index, ticks);
    }

    public Point2DLayer getLayer(Map<Integer, Integer> projections) {
        layer = fact.getLayer(projections);
        return this;
    }

    //Point2DLayer
    @Override
    public int size() {
        return layer.size();
    }

    @Override
    public float getX(int index) {
        return layer.getX(index);
    }

    @Override
    public float getY(int index) {
        return layer.getY(index);
    }

    @Override
    public float robustness(int index) {
        return layer.robustness(index);
    }

    @Override
    public float minX() {
        return layer.minX();
    }

    @Override
    public float maxX() {
        return layer.maxX();
    }

    @Override
    public float minY() {
        return layer.minY();
    }

    @Override
    public float maxY() {
        return layer.maxY();
    }
}
