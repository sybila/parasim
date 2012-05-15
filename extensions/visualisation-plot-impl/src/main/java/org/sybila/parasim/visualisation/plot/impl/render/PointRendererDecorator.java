package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Graphics2D;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 * Basis for classes which enhance existing renderers. In this basic variant
 * does not change the underlying renderer.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PointRendererDecorator implements PointRenderer {

    private PointRenderer renderer;

    /**
     * Initialize underlying renderer.
     * @param renderer Renderer used to render points.
     */
    public PointRendererDecorator(PointRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Specifies how the underlying renderer is modified according to rendered point.
     * @param canvas Swing class used to render points.
     * @param x Rendered point x coordinate.
     * @param y Rendered point y coordinate.
     * @param robustness Rendered point robustness.
     */
    protected void decorateCanvas(Graphics2D canvas, float x, float y, float robustness) {
        //do nothing
    }

    /**
     * Specifies how the x coordinate of point changes.
     * @param x Rendered point x coordinate.
     * @param y Rendered point y coordinate.
     * @param robustness Rendered point robustness.
     * @return New X coordinate of the point.
     */
    protected float decorateX(float x, float y, float robustness) {
        return x;
    }

    /**
     * Specifies how the y coordinate of point changes.
     * @param x Rendered point x coordinate.
     * @param y Rendered point y coordinate.
     * @param robustness Rendered point robustness.
     * @return New Y coordinate of the point.
     */
    protected float decorateY(float x, float y, float robustness) {
        return y;
    }

    /**
     * Specifies how the robustness of point changes.
     * @param x Rendered point x coordinate.
     * @param y Rendered point y coordinate.
     * @param robustness Rendered point robustness.
     * @return New robustness of the point.
     */
    protected float decorateRobustness(float x, float y, float robusntess) {
        return robusntess;
    }

    @Override
    public void drawPoint(Graphics2D canvas, float x, float y, float robustness) {
        decorateCanvas(canvas, x, y, robustness);
        renderer.drawPoint(canvas, decorateX(x, y, robustness), decorateY(x, y, robustness), decorateRobustness(x, y, robustness));
    }
}
