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
package org.sybila.parasim.visualisation.plot.impl;

import java.util.Map;

/**
 * Contains verification result projected into 2D. Rules the granularity of
 * unprojected axes. When position along unprojected axes is changed,
 * returns new {@link Point2DLayer} which stores rendered points.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface LayerFactory {

    /**
     * Returns granularity of given unprojected axis.
     * @param index Dimension of axis.
     * @return Number of discrete relevant positions on given axis.
     */
    public int ticks(int index);

    /**
     * Transforms real position on unprojected axis into its discrete value.
     * @param index Dimension of axis.
     * @param value Position on axis.
     * @return Discrete position on given axis.
     */
    public int getTicks(int index, float value);

    /**
     * Transforms discrete position on unprojected axis into float value.
     * @param index Dimension of axis.
     * @param ticks Position on axis.
     * @return Real position on given axis.
     */
    public float getValue(int index, int ticks);

    /**
     * Given discrete positions on all axes returns all rendered points.
     * @param projections Positions on unprojected axes -- pairs (dimension,position).
     * @return Rendered points encapsulated in {@link Point2DLayer}.
     */
    public Point2DLayer getLayer(Map<Integer, Integer> projections);
}
