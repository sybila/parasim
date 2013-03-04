/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
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
