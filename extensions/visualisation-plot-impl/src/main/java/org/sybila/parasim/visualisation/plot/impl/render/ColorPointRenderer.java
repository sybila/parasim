package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Color;
import java.awt.Graphics2D;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 * Enhances existing renderer, so that it renders points with positive
 * and negative robustness using different colors.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ColorPointRenderer extends PointRendererDecorator{

    private Color valid, invalid;

    /**
     * Specifies used color and underlying point renderer.
     * @param renderer Renderer used to render points.
     * @param valid Color for positive robustness.
     * @param invalid Color for negative robustness.
     */
    public ColorPointRenderer(PointRenderer renderer, Color valid, Color invalid) {
        super(renderer);
        this.valid = valid;
        this.invalid = invalid;
    }

    @Override
    protected void decorateCanvas(Graphics2D canvas, float x, float y, float robustness) {
        if (robustness > 0) {
            canvas.setPaint(valid);
        } else {
            canvas.setPaint(invalid);
        }
    }

}
