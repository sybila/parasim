package org.sybila.parasim.visualisation.plot.impl;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ResultPlotterConfiguration {

    private int plotterWindowHeight = 500;
    private int plotterWindowWidth = 750;
    private float minimumDifference = 0.001f;
    private int statusFontSize = 12;
    private int statusDecimalDigits = 3;
    private boolean showGuides = true;
    //RULE//
    private int ruleWidth = 45;
    private int ruleHeight = 30;
    private int ruleTickSpacing = 5;
    private int ruleTickRatio = 10;
    private int ruleBigTick = 10;
    private int ruleSmalltick = 5;
    private int ruleDecimalDigits = 2;

    public int getPlotterWindowHeight() {
        return plotterWindowHeight;
    }

    public int getPlotterWindowWidth() {
        return plotterWindowWidth;
    }

    public float getMinimumDifference() {
        return minimumDifference;
    }

    public int getStatusFontSize() {
        return statusFontSize;
    }

    public int getStatusDecimalDigits() {
        return statusDecimalDigits;
    }

    public boolean getShowGuides() {
        return showGuides;
    }

    public int getRuleWidth() {
        return ruleWidth;
    }

    public int getRuleHeight() {
        return ruleHeight;
    }

    public int getRuleBigTick() {
        return ruleBigTick;
    }

    public int getRuleDecimalDigits() {
        return ruleDecimalDigits;
    }

    public int getRuleSmalltick() {
        return ruleSmalltick;
    }

    public int getRuleTickRatio() {
        return ruleTickRatio;
    }

    public int getRuleTickSpacing() {
        return ruleTickSpacing;
    }
}
