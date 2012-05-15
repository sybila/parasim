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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 * Draws point as a circle. When the robustness is positive (i.e. property is valid),
 * it is green (red otherwise).
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated Used for testing purposes.
 */
@Deprecated
public class RGCirclePointRenderer implements PointRenderer {

    protected static final Color VALID = Color.GREEN;
    protected static final Color INVALID = Color.RED;
    protected static final int RADIUS = 3;

    @Override
    public void drawPoint(Graphics2D canvas, float x, float y, float robustness) {
        if (robustness > 0) {
            canvas.setPaint(VALID);
        } else {
            canvas.setPaint(INVALID);
        }
        canvas.fill(new Ellipse2D.Float(x-RADIUS, y+RADIUS, 2*RADIUS, 2*RADIUS));
    }


}
