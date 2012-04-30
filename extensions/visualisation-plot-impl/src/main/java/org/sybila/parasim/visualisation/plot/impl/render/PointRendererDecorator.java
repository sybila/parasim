package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Graphics2D;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PointRendererDecorator implements PointRenderer {

    private PointRenderer renderer;

    public PointRendererDecorator(PointRenderer renderer) {
        this.renderer = renderer;
    }

    protected void decorateCanvas(Graphics2D canvas, float x, float y, float robustness) {
        //do nothing
    }

    protected float decorateX(float x, float y, float robustness) {
        return x;
    }

    protected float decorateY(float x, float y, float robustness) {
        return y;
    }

    protected float decorateRobustness(float x, float y, float robusntess) {
        return robusntess;
    }

    @Override
    public void drawPoint(Graphics2D canvas, float x, float y, float robustness) {
        decorateCanvas(canvas, x, y, robustness);
        renderer.drawPoint(canvas, decorateX(x, y, robustness), decorateY(x, y, robustness), decorateRobustness(x, y, robustness));
    }
}
