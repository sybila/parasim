/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the
 *
 * @authors tag.
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

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.application.model.ExperimentLauncher;
import org.sybila.parasim.application.model.LoadedExperiment;
import org.sybila.parasim.application.model.TrajectoryAnalysisComputation;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
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
        ProjectManager projectManager = manager.resolve(ProjectManager.class, Default.class, manager.getRootContext());
        projectManager.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                manager.shutdown();
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        projectManager.setExperimentListener(new ExperimentListener() {
            @Override
            public void performExperiment(LoadedExperiment experiment) {
                try {
                    executeExperiment(manager, experiment);
                } catch (IOException e) {
                    LOGGER.error("Unable to execute experiment.", e);
                }
            }

            @Override
            public void showResult(LoadedExperiment experiment) {
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
        projectManager.setVisible(true);
    }

    private static void plotResult(final Manager manager, final LoadedExperiment experiment, VerificationResult result) throws XMLException {
        PlotterFactory strictPlotterFactory = manager.resolve(PlotterFactory.class, Strict.class, manager.getRootContext());
        final Plotter plotter = strictPlotterFactory.getPlotter(result, experiment.getOdeSystem());
        plotter.addMouseOnResultListener(new MouseOnResultListener() {
            @Override
            public void click(MouseOnResultListener.ResultEvent event) {
                Computation computation = new TrajectoryAnalysisComputation(plotter, event.getPoint(), experiment.getOdeSystem(), experiment.getFormula(), experiment.getPrecisionConfiguration(), experiment.getSimulationSpace());
                manager.resolve(ComputationContainer.class, Default.class, manager.getRootContext()).compute(computation);
            }
        });
        plotter.addPlotterWindowListener(new PlotterWindowListener() {
            @Override
            public void windowClosed(PlotterWindowListener.PlotterWindowEvent event) {
                manager.shutdown();
            }
        });
        plotter.plot();
    }

    private static void executeExperiment(Manager manager, LoadedExperiment experiment) throws IOException {
        // launch experiment
        VerificationResult result = null;
        try {
            result = ExperimentLauncher.launch(manager, experiment);
        } catch (Exception e) {
            LOGGER.error("Can't launch the experiment.", e);
            System.exit(1);
        }

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

        // plot result
        plotResult(manager, experiment, result);
    }
}