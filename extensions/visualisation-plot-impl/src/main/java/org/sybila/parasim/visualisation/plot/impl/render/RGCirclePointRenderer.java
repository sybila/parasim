package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
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
        canvas.fill(new Ellipse2D.Float(x - RADIUS, y - RADIUS, 2 * RADIUS, 2 * RADIUS));
    }
}
