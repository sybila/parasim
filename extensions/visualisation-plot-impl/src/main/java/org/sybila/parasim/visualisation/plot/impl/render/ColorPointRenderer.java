package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Color;
import java.awt.Graphics2D;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class ColorPointRenderer implements PointRenderer{

    private Color valid, invalid;

    public ColorPointRenderer(Color valid, Color invalid) {
        this.valid = valid;
        this.invalid = invalid;
    }

    @Override
    public void drawPoint(Graphics2D canvas, float x, float y, float robustness) {
        if (robustness > 0) {
            canvas.setPaint(valid);
        } else {
            canvas.setPaint(invalid);
        }
        drawPoint(canvas, x, y);
    }

    protected abstract void drawPoint(Graphics2D canvas, float x, float y);


}
