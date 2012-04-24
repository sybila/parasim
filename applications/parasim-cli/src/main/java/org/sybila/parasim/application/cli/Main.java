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

import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.application.model.ExperimentImpl;
import org.sybila.parasim.application.model.ExperimentLauncher;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.model.ode.OdeVariableMapping;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        ParasimOptions options;
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
            // check experiment file
            if (options.getExperimentFile() == null) {
                ParasimOptions.printHelp(System.out);
                System.exit(1);
            }
            // create manager
            Manager manager = null;
            try {
                manager = ManagerImpl.create();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                System.exit(1);
            }
            // load experiment
            Experiment experiment = null;
            try {
                 experiment = ExperimentImpl.fromPropertiesFile(options.getExperimentFile());
            } catch(IOException e) {
                LOGGER.error("Error during loading experiment file has happened.", e);
                System.exit(1);
            } catch(Exception e) {
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
            // plot result
            PlotterFactory plotterFactory = manager.resolve(PlotterFactory.class, Default.class, manager.getRootContext());
            plotterFactory.getPlotter(result, new OdeVariableMapping(experiment.getOdeSystem())).plot();
        } catch (ParseException ex) {
            ParasimOptions.printHelp(System.out);
            System.exit(1);
        }
    }
}