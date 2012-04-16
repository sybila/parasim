package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Graphics2D;

/**
 * Specifies appearance of rendered point, given associated robustness value.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PointRenderer {

    /**
     * Draw point on a given canvas.
     * @param canvas Canvas where the point is drawn.
     * @param x Point horizontal coordinate.
     * @param y Point vertical coordinate.
     * @param robustness Associated robustness value.
     */
    public void drawPoint(Graphics2D canvas, float x, float y, float robustness);
}
