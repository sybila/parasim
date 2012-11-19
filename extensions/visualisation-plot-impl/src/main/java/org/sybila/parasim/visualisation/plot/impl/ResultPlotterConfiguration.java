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

import java.awt.Color;

/**
 * Configuration pertaining to visualization extension.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ResultPlotterConfiguration {

    private int plotterWindowHeight = 500;
    private int plotterWindowWidth = 750;
    private float minimumDifference = 1E-15f;
    private int statusFontSize = 12;
    private int statusDecimalDigits = 3;
    private boolean showGuides = true;
    private float flatDimensionPadding = 0.1f;
    private Color guidesColor = Color.LIGHT_GRAY;
    //RULE//
    private int ruleWidth = 45;
    private int ruleHeight = 30;
    private int ruleTickSpacing = 5;
    private int ruleTickRatio = 10;
    private int ruleBigTick = 10;
    private int ruleSmalltick = 5;
    private int ruleDecimalDigits = 2;
    //RENDERER
    private Color pointColorValid = Color.GREEN;
    private Color pointColorInvalid = Color.RED;
    private float pointRadius = 3;

    /**
     * Height of plotter window.
     * @return Preferred height of plotter window.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.ProjectionPlotter
     */
    public int getPlotterWindowHeight() {
        return plotterWindowHeight;
    }

    /**
     * Width of plotter window.
     * @return Preferred width of plotter window.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.ProjectionPlotter
     */
    public int getPlotterWindowWidth() {
        return plotterWindowWidth;
    }

    /**
     * Having this difference, two floating point numbers are considered equal.
     * Introduced due to floating point imprecision.
     * @return Distance at which two floating point numbers are considered equal.
     */
    public float getMinimumDifference() {
        return minimumDifference;
    }

    /**
     * Status bar font size.
     * @return Size of font in the plotter window status bar.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.StatusBar
     */
    public int getStatusFontSize() {
        return statusFontSize;
    }

    /**
     * Decimal digits displayed by status bar.
     * @return Number of decimal digits displayed by coordinates in the plotter window status bar.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.StatusBar
     */
    public int getStatusDecimalDigits() {
        return statusDecimalDigits;
    }

    /**
     * Determines whether guiding lines are shown in plot area in plotter window.
     * @return <code>true</code> when they are visible, <code>false</code> otherwise.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.CanvasPane
     */
    public boolean getShowGuides() {
        return showGuides;
    }

    /**
     * Width of vertical rule delimiting plot area.
     * @return Width of vertical rule.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.CanvasPane
     */
    public int getRuleWidth() {
        return ruleWidth;
    }

    /**
     * Height of horizontal rule delimiting plot area.
     * @return Height of horizontal rule.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.Rule
     */
    public int getRuleHeight() {
        return ruleHeight;
    }

    /**
     * Length of big tick in a rule.
     * @return Big tick length.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.Rule
     */
    public int getRuleBigTick() {
        return ruleBigTick;
    }

    /**
     * Number of decimal digits displayed by a rule legend.
     * @return Number of decimal digits.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.Rule
     */
    public int getRuleDecimalDigits() {
        return ruleDecimalDigits;
    }

    /**
     * Length of small tick in a rule.
     * @return  Small tick length.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.Rule
     */
    public int getRuleSmalltick() {
        return ruleSmalltick;
    }

    /**
     * Ratio between small and big ticks in a rule.
     * @return Number of small ticks between two big ticks minus one.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.Rule
     */
    public int getRuleTickRatio() {
        return ruleTickRatio;
    }

    /**
     * Distance of two adjacent ticks in a rule.
     * @return Adjacent tick distance.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.Rule
     */
    public int getRuleTickSpacing() {
        return ruleTickSpacing;
    }

    /**
     * Color of valid points.
     * @return Color of points where monitored property is valid.
     * @see org.sybila.parasim.visualisation.plot.impl.render.ValidityPointRenderer
     */
    public Color getPointColorValid() {
        return pointColorValid;
    }

    /**
     * Color of invalid points.
     * @return Color of points where monitored property is invalid.
     * @see org.sybila.parasim.visualisation.plot.impl.render.ValidityPointRenderer
     */
    public Color getPointColorInvalid() {
        return pointColorInvalid;
    }

    /**
     * Size of displayed points.
     * @return Radius of points (displayed as circles).
     * @see org.sybila.parasim.visualisation.plot.impl.render.ValidityPointRenderer
     */
    public float getPointRadius() {
        return pointRadius;
    }

    /**
     * Amount of flat dimension padding. Dimension is considered flat
     * when all points have only one coordinate in this dimension.
     *
     * @return Padding by which is flat dimension is enlarged.
     * @see org.sybila.parasim.visualisation.plot.impl.SpaceUtils
     */
    public float getFlatDimensionPadding() {
        return flatDimensionPadding;
    }

    /**
     * Color of guiding lines.
     * @return Color of guiding lines in the plot area of plotter window.
     * @see org.sybila.parasim.visualisation.plot.impl.gui.CanvasPane
     */
    public Color getGuidesColor() {
        return guidesColor;
    }
}
