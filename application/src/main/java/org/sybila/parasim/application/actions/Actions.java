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
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Actions {

    private final Manager manager;
    private final ParasimOptions options;

    public Actions(Manager manager, ParasimOptions options) {
        this.manager = manager;
        this.options = options;
    }

    public ExecuteComputation compute(Experiment experiment) {
        return new ExecuteComputation(experiment, manager, options);
    }

    public ExportToCsv exportToCsv(VerificationResult result, Experiment experiment) {
        return new ExportToCsv(result, experiment, manager, options);
    }

    public LoadExperiment loadExperiment() {
        return new LoadExperiment(manager, options);
    }

    public PlotResults plotResults(VerificationResult result, Experiment experiment) {
        return new PlotResults(experiment, result, manager, options);
    }

    public Shutdown shutdown() {
        return new Shutdown(manager, options);
    }

    public StartGuiManager startGuiManager() {
        return new StartGuiManager(manager, options);
    }

    public StartServer startServer() {
        return new StartServer(manager, options);
    }

}
