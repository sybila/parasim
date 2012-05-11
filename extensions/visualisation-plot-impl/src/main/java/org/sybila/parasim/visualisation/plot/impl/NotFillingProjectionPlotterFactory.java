package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.AbstractVerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;
import org.sybila.parasim.visualisation.plot.impl.gui.ProjectionPlotter;
import org.sybila.parasim.visualisation.plot.impl.layer.EpsilonGridFactory;
import org.sybila.parasim.visualisation.plot.impl.layer.GridPointLayer;
import org.sybila.parasim.visualisation.plot.impl.layer.SimpleSingleLayerFactory;
import org.sybila.parasim.visualisation.plot.impl.render.RGCirclePointRenderer;
import org.sybila.parasim.visualisation.plot.impl.render.ZeroRemover;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NotFillingProjectionPlotterFactory implements PlotterFactory {

    private ResultPlotterConfiguration conf;

    public NotFillingProjectionPlotterFactory(ResultPlotterConfiguration conf) {
        this.conf = conf;
    }

    public Plotter getPlotter(VerificationResult result, PointVariableMapping names) {
        if (result.size() < 1) {
            return new EmptyPlotter();
        }
        if (result.getPoint(0).getDimension() < 2) {
            return new OneDimensionalPlotter();
        }
        OrthogonalSpace extent = AbstractVerificationResult.getEncompassingSpace(result);
        return new ProjectionPlotter(conf, result, names,
                new GridPointLayer(result, extent, EpsilonGridFactory.getCoordinateFactory(conf), new SimpleSingleLayerFactory()),
                new ZeroRemover(new RGCirclePointRenderer(), conf));
    }
}
