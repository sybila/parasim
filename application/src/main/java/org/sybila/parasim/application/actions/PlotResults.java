/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.application.actions;

import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.application.model.TrajectoryAnalysisComputation;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.MouseOnResultListener;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;
import org.sybila.parasim.visualisation.plot.api.PlotterWindowListener;
import org.sybila.parasim.visualisation.plot.api.annotations.Strict;

/**
 *
 * @author jpapouse
 */
public class PlotResults extends AbstractAction<Void> {

    private final VerificationResult result;
    private final Experiment experiment;

    public PlotResults(Experiment experiment, VerificationResult result, Manager manager, ParasimOptions options) {
        super(manager, options);
        this.result = result;
        this.experiment = experiment;
    }

    @Override
    public boolean isEnabled() {
        return !getOptions().isBatch() && getOptions().isTerminal() && !getOptions().isServer();
    }

    @Override
    public Void call() throws Exception {
        PlotterFactory strictPlotterFactory = getManager().resolve(PlotterFactory.class, Strict.class);
        final Plotter plotter = strictPlotterFactory.getPlotter(result, experiment.getOdeSystem());
        plotter.addMouseOnResultListener(new MouseOnResultListener() {
            @Override
            public void click(MouseOnResultListener.ResultEvent event) {
                Computation computation = new TrajectoryAnalysisComputation(plotter, event.getPoint(), experiment.getOdeSystem(), experiment.getFormula(), experiment.getPrecisionConfiguration(), experiment.getSimulationSpace());
                getManager().resolve(ComputationContainer.class, Default.class).compute(computation);
            }
        });
        plotter.addPlotterWindowListener(new PlotterWindowListener() {
            @Override
            public void windowClosed(PlotterWindowListener.PlotterWindowEvent event) {
                try {
                    getManager().destroy();
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        plotter.plot();
        return null;
    }

}
