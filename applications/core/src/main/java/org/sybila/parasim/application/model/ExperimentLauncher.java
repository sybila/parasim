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
package org.sybila.parasim.application.model;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.ode.OdeVariableMapping;
import org.sybila.parasim.model.verification.result.VerificationResult;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExperimentLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentLauncher.class);

    private ExperimentLauncher() {
    }

    public static VerificationResult launch(Manager manager, Experiment experiment) throws Exception {
        ComputationContainer container = manager.resolve(ComputationContainer.class, Default.class, manager.getRootContext());
        experiment.getPrecisionConfigurationResources().load();
        experiment.getInitialSamplingResource().load();
        experiment.getSimulationSpaceResource().load();
        experiment.getInitialSpaceResource().load();
        experiment.getSTLFormulaResource().setVariableMapping(new OdeVariableMapping(experiment.getOdeSystem()));
        experiment.getSTLFormulaResource().load();
        for (OdeSystemVariable variable: experiment.getOdeSystem()) {
            LOGGER.info(variable.getName() + "' = " + variable.getRightSideExpression().toFormula());
        }
        return container.compute(new ValidityRegionsComputation(
                experiment.getOdeSystem(),
                experiment.getPrecisionConfigurationResources().getRoot(),
                experiment.getInitialSamplingResource().getRoot(),
                experiment.getSimulationSpaceResource().getRoot(),
                experiment.getInitialSpaceResource().getRoot(),
                experiment.getSTLFormulaResource().getRoot(),
                experiment.getIterationLimit())).get(experiment.getTimeoutInMilliSeconds(), TimeUnit.MILLISECONDS);
    }

}
