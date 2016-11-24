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

/**
 * Contains verification result which is projected into 2D. When
 * axes of projection are changed, new {@link LayerFactory} is produced.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface LayerMetaFactory {

    /**
     * When axis of projection are changed, new {@link LayerFactory} is produced.
     * @param xAxis Dimension for horizontal axis.
     * @param yAxis Dimension for vertical axis.
     * @return {@link LayerFactory} which projects points along given axes.
     */
    public LayerFactory getLayerFactory(int xAxis, int yAxis);
}
