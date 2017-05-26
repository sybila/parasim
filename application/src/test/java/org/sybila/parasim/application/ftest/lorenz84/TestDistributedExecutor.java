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
package org.sybila.parasim.application.ftest.lorenz84;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.cli.ParseException;
import org.sybila.parasim.application.ParasimOptions;
import org.sybila.parasim.application.actions.Actions;
import org.sybila.parasim.extension.projectmanager.api.Experiment;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.DistributedMemoryExecutor;
import org.sybila.parasim.core.annotation.Default;
import org.sybila.parasim.core.test.RemoteParasimTest;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestDistributedExecutor extends RemoteParasimTest {

    @Test(enabled = false)
    public void testSimple() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Experiment experiment = TestLorenz84Oscilation.loadExperiment();
        Point initialPoint = new ArrayPoint(0f, 0f, 0f, 0f, 1.75f, 0.5625f);
        VerificationResult timeNeeded = getManager().resolve(ComputationContainer.class, Default.class).compute(new TestLorenz84Oscilation.RobustnessComputation(
                initialPoint,
                experiment.getFormula().getTimeNeeded(),
                experiment.getOdeSystem(),
                experiment.getPrecisionConfiguration(),
                experiment.getSimulationSpace(),
                experiment.getFormula())).get(40, TimeUnit.SECONDS);
        VerificationResult simulationTime = getManager().resolve(ComputationContainer.class, Default.class).compute(new TestLorenz84Oscilation.RobustnessComputation(
                initialPoint,
                experiment.getSimulationSpace().getMaxBounds().getTime(),
                experiment.getOdeSystem(),
                experiment.getPrecisionConfiguration(),
                experiment.getSimulationSpace(),
                experiment.getFormula())).get(40, TimeUnit.SECONDS);
        Assert.assertEquals(timeNeeded.getRobustness(0).getValue(), simulationTime.getRobustness(0).getValue(), 0.005f, "Analysis shouldn't be dependent on simulation length.");
    }

    @Test(enabled = false)
    public void testReal() throws ParseException, Exception {
        String[] args = new String[] {"-t", "-e", TestDistributedExecutor.class.getClassLoader().getResource("org/sybila/parasim/application/ftest/lorenz84/experiment-oscil.properties").getFile(), "-b"};
        ParasimOptions options = ParasimOptions.create(args);
        Actions actions = new Actions(getManager(), options);
        Experiment experiment = actions.loadExperiment().call();
        VerificationResult result = actions.compute(experiment).call();
        Assert.assertNotNull(result);
    }

    @Override
    protected void beforeManagerCreated() {
        super.beforeManagerCreated();
        System.setProperty("parasim.computation.lifecycle.default.executor", DistributedMemoryExecutor.class.getName());
    }

    @AfterMethod
    public void resetProperties() {
        System.clearProperty("parasim.computation.lifecycle.default.executor");
    }

}
