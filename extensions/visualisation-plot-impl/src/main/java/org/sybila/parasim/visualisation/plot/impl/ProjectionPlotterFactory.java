package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectionPlotterFactory implements PlotterFactory {

    public Plotter getPlotter(VerificationResult result, PointVariableMapping mapping) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
