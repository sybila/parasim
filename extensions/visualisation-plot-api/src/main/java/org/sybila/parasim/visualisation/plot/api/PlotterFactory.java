package org.sybila.parasim.visualisation.plot.api;

import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * Creates {@link Plotter} objects from {@link VerificationResult}.
 * Core class of extension -- should be created at initialization.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PlotterFactory {

    /**
     * Creates a plotter from result.
     * @param result Object to be plotted.
     * @param mapping Mapping of names to variables (for labels).
     * @return Plotter which plots designated result.
     */
    public Plotter getPlotter(VerificationResult result, PointVariableMapping mapping);
}
