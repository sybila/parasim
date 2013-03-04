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
import org.sybila.parasim.application.model.ExperimentImpl;
import org.sybila.parasim.core.api.Manager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LoadExperiment extends AbstractAction<Experiment> {

    public LoadExperiment(Manager manager, ParasimOptions options) {
        super(manager, options);
    }

    @Override
    public boolean isEnabled() {
        return (getOptions().isResultOnly() || getOptions().isTerminal()) && !getOptions().isServer() && !getOptions().isHelp();
    }

    @Override
    public Experiment call() throws Exception {
        if (getOptions().getExperimentFile() == null) {
            System.err.println("Experiment file not specified.");
            ParasimOptions.printHelp(System.out);
            System.exit(1);
        }
        return ExperimentImpl.fromPropertiesFile(getOptions().getExperimentFile());
    }

}
