package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Graphics2D;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PointRenderer {

    public void drawPoint(Graphics2D canvas, float x, float y, float robustness);
}
