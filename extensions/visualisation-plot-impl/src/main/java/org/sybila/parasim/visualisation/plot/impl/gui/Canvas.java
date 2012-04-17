/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 * Draws points on a 2D canvas. Handles coordinate transformation and resizing.
 * The appearance of point is handled by {@link PointRenderer}.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Canvas extends JPanel {

    //drawing constants
    protected static final Color BLANK = Color.WHITE;
    private static final int PADDING = 10;
    //attributes
    private Point2DLayer points = null;
    private float xFact, yFact;
    private PointRenderer render;

    /**
     * Create new canvas with given point appearance.
     *
     * @param renderer Class handling point appearance.
     */
    public Canvas(PointRenderer renderer) {
        render = renderer;
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                if (points != null) {
                    refreshFactors();
                }
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        // clean //
        Graphics2D canvas = (Graphics2D) g;
        canvas.setBackground(BLANK);
        canvas.clearRect(0, 0, getWidth(), getHeight());

        // draw points //
        if (points != null) {
            for (int i = 0; i < points.size(); i++) {
                render.drawPoint(canvas, transformX(points.getX(i)), transformY(points.getY(i)), points.robustness(i));
            }
        }
    }

    /**
     * Designates points to be rendered. Forces repaint.
     *
     * @param layer Rendered points.
     */
    public void setPoints(Point2DLayer layer) {
        points = layer;
        refreshFactors();
        repaint();
    }

    /**
     * Called to refresh transformation factors.
     */
    private void refreshFactors() {
        xFact = (getWidth() - 2 * PADDING) / (points.maxX() - points.minX());
        yFact = (getHeight() - 2 * PADDING) / (points.maxY() - points.minY());
    }

    private float transformX(float x) {
        return (x - points.minX()) * xFact + PADDING;
    }

    private float transformY(float y) {
        return getHeight() - (y - points.minY()) * yFact - PADDING;
    }
}
