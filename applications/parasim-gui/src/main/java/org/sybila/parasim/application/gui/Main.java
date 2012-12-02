/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.application.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.application.model.ExperimentLauncher;
import org.sybila.parasim.application.model.TrajectoryAnalysisComputation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.extension.progresslogger.api.LoggerWindow;
import org.sybila.parasim.extension.progresslogger.api.ProgressLogger;
import org.sybila.parasim.extension.projectmanager.api.ExperimentListener;
import org.sybila.parasim.extension.projectmanager.api.ProjectManager;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLResource;
import org.sybila.parasim.visualisation.plot.api.MouseOnResultListener;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;
import org.sybila.parasim.visualisation.plot.api.PlotterWindowListener;
import org.sybila.parasim.visualisation.plot.api.annotations.Strict;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, Exception {
        final Manager manager = ManagerImpl.create();
        manager.start();
        ProgressLogger progressLogger = manager.resolve(ProgressLogger.class, Default.class, manager.getRootContext());
        final LoggerWindow loggerWindow = progressLogger.getLoggerWindow();
        ProjectManager projectManager = manager.resolve(ProjectManager.class, Default.class, manager.getRootContext());
        projectManager.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                loggerWindow.dispose();
                if (manager.isRunning()) {
                    manager.shutdown();
                }
                System.exit(0);
            }
        });
        projectManager.setExperimentListener(new ExperimentListener() {

            @Override
            public void performExperiment(Experiment experiment) {
                try {
                    executeExperiment(manager, experiment, loggerWindow);
                } catch (IOException e) {
                    LOGGER.error("Unable to execute experiment.", e);
                }
            }

            @Override
            public void showResult(Experiment experiment) {
                XMLResource<VerificationResult> input = experiment.getVerificationResultResource();
                try {
                    input.load();
                    plotResult(manager, experiment, input.getRoot());
                } catch (XMLException e) {
                    LOGGER.error("Unable to show result.", e);
                    System.exit(1);
                }
            }
        });
        loggerWindow.setVisible(true);
        projectManager.setVisible(true);
    }

    private static void plotResult(final Manager manager, final Experiment experiment, VerificationResult result) throws XMLException {
        PlotterFactory strictPlotterFactory = manager.resolve(PlotterFactory.class, Strict.class, manager.getRootContext());
        final Plotter plotter = strictPlotterFactory.getPlotter(result, experiment.getOdeSystem());
        plotter.addMouseOnResultListener(new MouseOnResultListener() {

            @Override
            public void click(MouseOnResultListener.ResultEvent event) {
                Computation computation = new TrajectoryAnalysisComputation(plotter, event.getPoint(), experiment.getOdeSystem(), experiment.getFormula(), experiment.getPrecisionConfiguration(), experiment.getSimulationSpace());
                manager.resolve(ComputationContainer.class, Default.class, manager.getRootContext()).compute(computation);
            }
        });
        plotter.plot();
    }

    private static void executeExperiment(final Manager manager, final Experiment experiment, final LoggerWindow logger) throws IOException {
        new SwingWorker<VerificationResult, Object>() {

            @Override
            public VerificationResult doInBackground() {
                // launch experiment
                VerificationResult result = null;
                logger.simulationStarted();
                try {
                    result = ExperimentLauncher.launch(manager, experiment);
                } catch (Exception e) {
                    LOGGER.error("Can't launch the experiment.", e);
                    System.exit(1);
                }
                logger.simulationStopped();

                //save result
                XMLResource<VerificationResult> output = experiment.getVerificationResultResource();
                if (output != null) {
                    output.setRoot(result);
                    try {
                        output.store();
                    } catch (XMLException xmle) {
                        LOGGER.error("Unable to store result.", xmle);
                        System.exit(1);
                    }
                }
                return result;
            }

            @Override
            protected void done() {
                try {
                    // plot result
                    plotResult(manager, experiment, get());
                } catch (InterruptedException ie) {
                    LOGGER.error("Simulation was interrupted.", ie);
                } catch (ExecutionException ee) {
                    LOGGER.error("Simulation was aborted.", ee);
                } catch (XMLException xmle) {
                    LOGGER.error("XML exception during simulation.", xmle);
                }
            }
        }.execute();
    }
}