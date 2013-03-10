/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.application.model;

import org.sybila.parasim.extension.projectmanager.api.Experiment;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.Future;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.api.Resolver;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stlstar.FormulaStarInfo;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ExperimentLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExperimentLauncher.class);

    private ExperimentLauncher() {
    }

    public static VerificationResult launch(Resolver resolver, Experiment experiment) throws Exception {
        ComputationContainer container = resolver.resolve(ComputationContainer.class, Default.class);
        for (OdeSystemVariable variable : experiment.getOdeSystem()) {
            LOGGER.info(variable.getName() + "' = " + variable.getRightSideExpression().toFormula());
        }
        LOGGER.info(getFormulaInfo(experiment.getFormula()));
        Future<VerificationResult> result = container.compute(new ValidityRegionsComputation(
                experiment.getOdeSystem(),
                experiment.getPrecisionConfiguration(),
                experiment.getSimulationSpace(),
                experiment.getInitialSpace(),
                experiment.getFormula(),
                experiment.getIterationLimit()));
        try {
            return result.get(experiment.getTimeoutAmount(), experiment.getTimeoutUnit());
        } catch (TimeoutException e) {
            LOGGER.error("timeout");
            result.cancel(true);
            return result.getPartial();
        }
    }

    private static String getFormulaInfo(Formula target) {
        StringBuilder info = new StringBuilder(target.toString());
        info.append(" (star number: ");
        FormulaStarInfo starInfo = new FormulaStarInfo(target);
        info.append(starInfo.getStarNumber());
        info.append(")");
        return info.toString();
    }
}
