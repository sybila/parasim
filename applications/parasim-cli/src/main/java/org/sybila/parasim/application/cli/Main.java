/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.application.cli;

import java.io.File;
import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.application.model.ExperimentImpl;
import org.sybila.parasim.application.model.ExperimentLauncher;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.model.ode.OdeVariableMapping;
import org.sybila.parasim.model.ode.PointVariableIdentity;
import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResultResource;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;
import org.sybila.parasim.visualisation.plot.api.annotations.Filling;
import org.sybila.parasim.visualisation.plot.api.annotations.Strict;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static Manager manager = null;
    private static ParasimOptions options = null;

    public static void main(String[] args) throws IOException {
        try {
            // load options
            options = ParasimOptions.create(args);
            // print help
            if (options.isHelp()) {
                ParasimOptions.printHelp(System.out);
                System.exit(0);
            }
            // print version
            if (options.isVersion()) {
                ParasimOptions.printVersion(System.out);
                System.exit(0);
            }

            // check input
            if (options.getExperimentFile() == null && options.getResultFile() == null) {
                ParasimOptions.printHelp(System.out);
                System.exit(1);
            }

            // create manager
            try {
                manager = ManagerImpl.create();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                System.exit(1);
            }

            if (options.getExperimentFile() != null) {
                executeExperiment();
            } else {
                assert options.getResultFile() == null;
                showResult(options.getResultFile());
            }


        } catch (ParseException ex) {
            ParasimOptions.printHelp(System.out);
            System.exit(1);
        }
    }

    private static void executeExperiment() {
        // load experiment
        Experiment experiment = null;
        try {
            experiment = ExperimentImpl.fromPropertiesFile(options.getExperimentFile());
        } catch (IOException e) {
            LOGGER.error("Error during loading experiment file has happened.", e);
            System.exit(1);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        }
        // start manager
        manager.start();
        // launch experiment
        VerificationResult result = null;
        try {
            result = ExperimentLauncher.launch(manager, experiment);
        } catch (Exception e) {
            LOGGER.error("Can't launch the experiment.", e);
            System.exit(1);
        }

        //save result
        VerificationResultResource output = experiment.getVerificationResultResource();
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
        plotResult(result, new OdeVariableMapping(experiment.getOdeSystem()));
    }

    private static void showResult(String filename) {
        VerificationResultResource input = new VerificationResultResource(new File(filename));
        try {
            input.load();
        } catch (XMLException xmle) {
            LOGGER.error("Unable to load result.", xmle);
            System.exit(1);
        }
        manager.start();
        plotResult(input.getRoot(), new PointVariableIdentity());
    }

    private static void plotResult(VerificationResult result, PointVariableMapping mapping) {
        PlotterFactory strictPlotterFactory = manager.resolve(PlotterFactory.class, Strict.class, manager.getRootContext());
        PlotterFactory fillingPlotterFactory = manager.resolve(PlotterFactory.class, Filling.class, manager.getRootContext());
        strictPlotterFactory.getPlotter(result, mapping).plot();
        fillingPlotterFactory.getPlotter(result, mapping).plot();
    }
}