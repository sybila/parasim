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
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 * Tracks mouse motion across the plot area, sending it to designed listener.
 * Renders guiding lines.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class CanvasPane extends JRootPane {

    /**
     * Listens to change of mouse position.
     */
    public static interface PositionChangeListener {

        /**
         * Called when mouse position was changed.
         * @param x X coordinate of new mouse position.
         * @param y Y coordinate of new mouse position.
         */
        public void updatePosition(float x, float y);
    }
    //non-static
    private Point position;
    private Canvas contents;
    private PositionChangeListener update;

    /**
     * Class responsible for drawing guiding lines.
     */
    private class Overlay extends JComponent {

        private Color guides;

        public Overlay(Color guideColor) {
            guides = guideColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Rectangle bounds = g.getClipBounds();

            if (position != null) {
                g.setColor(guides);
                g.drawLine(position.x, bounds.y, position.x, bounds.y + bounds.height);
                g.drawLine(bounds.x, position.y, bounds.x + bounds.width, position.y);
            }
        }
    }

    /**
     * Initiate object. Uses following configurable values:
     * <ul>
     *  <li>{@link ResultPlotterConfiguration#getGuidesColor()} as guiding lines color.</li>
     *  <li>{@link ResultPlotterConfiguration#getShowGuides()} to determine whether guiding lines should be rendered.</li>
     * </ul>
     *
     * @param conf This extension configuration.
     * @param canvas Contained plot area.
     * @param onUpdate Listener notified when mouse position is updated.
     */
    public CanvasPane(ResultPlotterConfiguration conf, Canvas canvas, PositionChangeListener onUpdate) {
        contents = canvas;
        update = onUpdate;
        setContentPane(canvas);

        setGlassPane(new Overlay(conf.getGuidesColor()));
        getGlassPane().setVisible(conf.getShowGuides());

        MouseAdapter mouseActions = new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                moveMouse(e.getPoint());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                moveMouse(e.getPoint());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                position = null;
                getGlassPane().repaint();
            }
        };
        addMouseListener(mouseActions);
        addMouseMotionListener(mouseActions);
    }

    /**
     * Updates guides and notifies listener.
     */
    private void moveMouse(Point position) {
        this.position = position;
        update.updatePosition(contents.getX(position), contents.getY(position));
        getGlassPane().repaint();
    }
}
