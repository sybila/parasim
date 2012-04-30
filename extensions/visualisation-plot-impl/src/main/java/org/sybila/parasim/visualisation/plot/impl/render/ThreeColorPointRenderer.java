package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Color;
import java.awt.Graphics2D;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 * One color for negative robustness, one for positive and one for zero.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ThreeColorPointRenderer extends PointRendererDecorator {

    private Color valid, invalid, zero;
    private float epsilon;

    public ThreeColorPointRenderer(PointRenderer renderer, ResultPlotterConfiguration conf, Color valid, Color invalid, Color zero) {
        super(renderer);
        this.valid = valid;
        this.invalid = invalid;
        this.zero = zero;
        epsilon = conf.getMinimumDifference();
    }

    @Override
    protected void decorateCanvas(Graphics2D canvas, float x, float y, float robustness) {
        if (Math.abs(robustness) < epsilon) {
            canvas.setPaint(zero);
        } else if (robustness > 0) {
            canvas.setPaint(valid);
        } else {
            canvas.setPaint(invalid);
        }
    }





}
