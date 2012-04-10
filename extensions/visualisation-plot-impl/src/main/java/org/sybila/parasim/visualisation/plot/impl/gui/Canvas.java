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
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 * Draws points on a 2D canvas. Handles coordinate transformation and resizing.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Canvas extends JPanel {

    protected static final Color BLANK = Color.WHITE;
    private Point2DLayer points = null;

    @Override
    public void paint(Graphics g) {
        // clean //
        Graphics2D canvas = (Graphics2D) g;
        canvas.setBackground(BLANK);
        canvas.clearRect(0, 0, getWidth(), getHeight());
    }

    /**
     * Designates points to be rendered. Forces repaint.
     * @param layer Rendered points.
     */
    public void setPoints(Point2DLayer layer) {
        points = layer;
    }

}
