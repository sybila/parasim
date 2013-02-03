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
package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Color;
import java.awt.Graphics2D;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 * One color for negative robustness, one for positive and one for zero.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ThreeColorPointRenderer extends PointRendererDecorator {

    private Color valid, invalid, zero;
    private float epsilon;

    /**
     * Initializes underlying renderer and colors.
     * @param renderer Underlying renderer.
     * @param conf This extension configuration.
     * @param valid Color for valid points.
     * @param invalid Color for invalid points.
     * @param zero Color for points with zero robustness.
     */
    public ThreeColorPointRenderer(PointRenderer renderer, ResultPlotterConfiguration conf, Color valid, Color invalid, Color zero) {
        super(renderer);
        this.valid = valid;
        this.invalid = invalid;
        this.zero = zero;
        epsilon = conf.getMinimumDifference();
    }

    @Override
    protected void decorateCanvas(Graphics2D canvas, float x, float y, float robustness) {
        if (Math.abs(robustness) < epsilon) {
            canvas.setPaint(zero);
        } else if (robustness > 0) {
            canvas.setPaint(valid);
        } else {
            canvas.setPaint(invalid);
        }
    }





}
