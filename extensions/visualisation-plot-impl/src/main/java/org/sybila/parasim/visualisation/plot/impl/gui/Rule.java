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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.plaf.DimensionUIResource;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 * A rule displaying axis with legend in the form of ticks with associated numbers (in model coordinates).
 * Can be either horizontal or vertical.
 *
 * Each rule is composed of big ticks spaced evenly along the border (plot area) and labeled
 * by associated model coordinates. Space between big ticks is filled by small ticks (unlabeled).
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Rule extends JPanel {

    /**
     * Rule orientation.
     */
    public static enum Orientation {

        /** Horizontal orientation */
        HORIZONTAL,
        /** Vertical orientation */
        VERTICAL;

        /**
         * Determine whether this orientation is horizontal.
         * @return <code>true</code> when this orientation is horizontal, <code>false</code> otherwise.
         */
        public boolean isHorizontal() {
            return equals(Orientation.HORIZONTAL);
        }
    }
    private static final Color BLANK = Color.WHITE;
    private static final Color TICKS = Color.BLACK;
    private static final int PADDING = 5;
    // non-static //
    private ResultPlotterConfiguration conf;
    private Orientation orient;
    private float min, max;
    private DecimalFormat format;

    /**
     * Initializes the rule and sets its orientation.
     * There are several configurable values (see {@link ResultPlotterConfiguration}).
     * @param conf This extension configuration.
     * @param orientation This rule orientation.
     */
    public Rule(ResultPlotterConfiguration conf, Orientation orientation) {
        this.conf = conf;
        orient = orientation;

        setLayout(null);

        if (orient.isHorizontal()) {
            setPreferredSize(new Dimension(Integer.MAX_VALUE, conf.getRuleHeight()));
        } else {
            setPreferredSize(new DimensionUIResource(conf.getRuleWidth(), Integer.MAX_VALUE));
        }

        StringBuilder formatBuilder = new StringBuilder("0.");
        for (int i = 0; i < conf.getRuleDecimalDigits(); i++) {
            formatBuilder.append('#');
        }
        format = new DecimalFormat(formatBuilder.toString());
    }

    /**
     * Update scale of this rule.
     * @param min Minimum model coordinate.
     * @param max Maximum model coordinate.
     */
    public void update(float min, float max) {
        this.min = min;
        this.max = max;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D canvas = (Graphics2D) g;
        Rectangle bounds = canvas.getClipBounds();

        canvas.setColor(BLANK);
        canvas.fill(bounds);

        canvas.setColor(TICKS);

        int start;
        if (orient.isHorizontal()) {
            start = bounds.x + Canvas.PADDING;
        } else {
            start = bounds.y + bounds.height - Canvas.PADDING;
        }
        int size = (orient.isHorizontal() ? bounds.width : bounds.height) - 2 * Canvas.PADDING;
        int numTicks = size / conf.getRuleTickSpacing() + 1;

        for (int i = 0; i < numTicks; i++) {
            int length = (i % conf.getRuleTickRatio() == 0) ? conf.getRuleBigTick() : conf.getRuleSmalltick();
            int span = i * conf.getRuleTickSpacing();
            int coord = start;
            if (orient.isHorizontal()) {
                coord += span;
                canvas.drawLine(coord, bounds.x, coord, bounds.x + length);
            } else {
                coord -= span;
                int end = bounds.y + bounds.width;
                canvas.drawLine(end - length, coord, end, coord);
            }
        }
        int numBigTicks = size / conf.getRuleTickSpacing() / conf.getRuleTickRatio() + 1;
        float perTick = (max - min) * conf.getRuleTickSpacing() * conf.getRuleTickRatio() / size;

        //g.setFont(new Font("SansSerif", Font.PLAIN, 10));

        for (int i = 0; i < numBigTicks; i++) {
            String label = format.format(min + i * perTick);
            int span = i * conf.getRuleTickSpacing() * conf.getRuleTickRatio();
            int coord = start;
            if (orient.isHorizontal()) {
                coord += span;
                g.drawString(label, coord, bounds.y + bounds.height - PADDING);
            } else {
                coord -= span;
                g.drawString(label, bounds.y + PADDING, coord);
            }
        }
    }
}
