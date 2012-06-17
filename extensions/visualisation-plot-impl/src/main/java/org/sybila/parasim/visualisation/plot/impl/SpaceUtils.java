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
package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;

/**
 * Utility class adjusting {@link OrthogonalSpace} for the use in this extension.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SpaceUtils {

    private float epsilon;
    private float padding;

    /**
     * Initializes inner variables according to extension configuration.
     * @param conf This extension configuration.
     */
    public SpaceUtils(ResultPlotterConfiguration conf) {
        epsilon = conf.getMinimumDifference();
        padding = conf.getFlatDimensionPadding();
    }

    /**
     * Finds all flat dimensions in a space and enlarges them with a padding.
     * This is needed for flat dimensions to be displayed properly.
     *
     * A dimension is considered flat when all points have only one coordinate
     * in the given dimension.
     *
     * Uses the following configurable values:
     * <ul>
     * <li>{@link ResultPlotterConfiguration#getMinimumDifference()} to find flat dimensions.</li>
     * <li>{@link ResultPlotterConfiguration#getFlatDimensionPadding() -- width of added padding.</li>
     * </ul>
     *
     * @param src Space which should be provided with padding.
     * @return New space which is the copy of <code>src</code> with the exception of flat dimensions.
     */
    OrthogonalSpace provideWithPadding(OrthogonalSpace src) {
        int dim = src.getDimension();
        float[] upper = new float[dim];
        float[] lower = new float[dim];
        for (int i = 0; i < dim; i++) {
            upper[i] = src.getMaxBounds().getValue(i);
            lower[i] = src.getMinBounds().getValue(i);
            if (Math.abs(upper[i] - lower[i]) < epsilon) { //too close, need padding
                upper[i] += padding;
                lower[i] -= padding;
            }
        }

        return new OrthogonalSpace(new ArrayPoint(0, lower, 0, dim), new ArrayPoint(0, upper, 0, dim));
    }
}
