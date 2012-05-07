package org.sybila.parasim.visualisation.plot.impl.render;

import java.awt.Graphics2D;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.visualisation.plot.impl.gui.PointRenderer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ZeroRemover implements PointRenderer {
    private PointRenderer renderer;
    private float epsilon;

    public ZeroRemover(PointRenderer renderer, ResultPlotterConfiguration conf) {
        this.renderer = renderer;
        epsilon = conf.getMinimumDifference();
    }

    public void drawPoint(Graphics2D canvas, float x, float y, float robustness) {
        if (!(Math.abs(robustness) < epsilon)) {
            renderer.drawPoint(canvas, x, y, robustness);
        }
    }


}
