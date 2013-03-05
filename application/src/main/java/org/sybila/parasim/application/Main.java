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
package org.sybila.parasim.application;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.application.actions.Actions;
import org.sybila.parasim.extension.projectmanager.api.Experiment;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.core.impl.ManagerImpl;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Experiment experiment = null;
        VerificationResult result = null;
        ParasimOptions options = ParasimOptions.create(args);
        if (options.getConfigFile() != null) {
            System.setProperty("parasim.config.file", options.getConfigFile());
        }
        Manager manager = ManagerImpl.create().start();
        Actions actions = new Actions(manager, options);
        try {
            if (actions.help().isEnabled()) {
                actions.help().call();
            }
            if (actions.loadExperiment().isEnabled()) {
                experiment = actions.loadExperiment().call();
            }
            if (actions.compute(experiment).isEnabled()) {
                result = actions.compute(experiment).call();
            }
            if (actions.loadResults(experiment).isEnabled()) {
                result = actions.loadResults(experiment).call();
            }
            if (actions.plotResults(result, experiment).isEnabled()) {
                actions.plotResults(result, experiment).call();
            }
            if (actions.exportToCsv(result, experiment).isEnabled()) {
                actions.exportToCsv(result, experiment).call();
            }
            if (actions.startServer().isEnabled()) {
                actions.startServer().call();
            }
            if (actions.startGuiManager().isEnabled()) {
                actions.startGuiManager().call();
            }
        } catch (ParseException e) {
            actions.help().call();
        } finally {
            if (actions.shutdown().isEnabled()) {
                actions.shutdown().call();
            }
        }
    }
}