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
package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Graphics2D;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 * Point renderer which removes all points with robustness equal to zero before
 * passing them to another renderer.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ZeroRemover implements PointRenderer {
    private PointRenderer renderer;
    private float epsilon;

    /**
     * Initialize underlying point renderer.
     * @param renderer Renderer used to render points after zero robustness points are removed.
     * @param conf This extension configuration.
     */
    public ZeroRemover(PointRenderer renderer, ResultPlotterConfiguration conf) {
        this.renderer = renderer;
        epsilon = conf.getMinimumDifference();
    }

    @Override
    public void drawPoint(Graphics2D canvas, float x, float y, float robustness) {
        if (!(Math.abs(robustness) < epsilon)) {
            renderer.drawPoint(canvas, x, y, robustness);
        }
    }


}
