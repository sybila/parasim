/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
import org.sybila.parasim.application.model.ExperimentLauncher;
import org.sybila.parasim.core.api.Manager;
import org.sybila.parasim.extension.projectmanager.api.Experiment;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLResource;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExecuteComputation extends AbstractAction<VerificationResult> {

    private final Experiment experiment;

    public ExecuteComputation(Experiment experiment, Manager manager, ParasimOptions options) {
        super(manager, options);
        this.experiment = experiment;
    }

    @Override
    public boolean isEnabled() {
        return getOptions().isTerminal() && !getOptions().isServer() && !getOptions().isHelp() && !getOptions().isResultOnly();
    }

    @Override
    public VerificationResult call() throws Exception {
        VerificationResult result = ExperimentLauncher.launch(getManager(), experiment);
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

}
