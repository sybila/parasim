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
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 * Enhances existing renderer, so that it renders points with positive
 * and negative robustness using different colors.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ColorPointRenderer extends PointRendererDecorator{

    private Color valid, invalid;

    /**
     * Specifies used color and underlying point renderer.
     * @param renderer Renderer used to render points.
     * @param valid Color for positive robustness.
     * @param invalid Color for negative robustness.
     */
    public ColorPointRenderer(PointRenderer renderer, Color valid, Color invalid) {
        super(renderer);
        this.valid = valid;
        this.invalid = invalid;
    }

    @Override
    protected void decorateCanvas(Graphics2D canvas, float x, float y, float robustness) {
        if (robustness > 0) {
            canvas.setPaint(valid);
        } else {
            canvas.setPaint(invalid);
        }
    }

}
