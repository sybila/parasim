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
package org.sybila.parasim.application.ftest.lorenz84;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.sybila.parasim.application.model.Experiment;
import org.sybila.parasim.application.model.ExperimentImpl;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.lifecycle.api.annotations.RunWith;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.annotations.SimulationSpace;
import org.sybila.parasim.computation.verification.api.STLVerifier;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.execution.api.SharedMemoryExecutor;
import org.sybila.parasim.execution.api.annotations.NumberOfInstances;
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeVariableMapping;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.result.AbstractVerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@NumberOfInstances(1)
@RunWith(executor=SharedMemoryExecutor.class)
public class TestLorenz84Oscilation {

    private static Manager manager;

    @BeforeClass
    public static void startManager() throws Exception {
        manager = ManagerImpl.create();
        manager.start();
    }

    public static Manager getManager() {
        return manager;
    }

    @AfterClass
    public static void stopManager() {
        manager.shutdown();
    }

    @Test
    public void testSimple() throws IOException, InterruptedException, ExecutionException {
        Experiment experiment = loadExperiment();
        Point initialPoint = new ArrayPoint(0f, 0f, 0f, 0f, 1.75f, 0.5625f);
        VerificationResult timeNeeded = getManager().resolve(ComputationContainer.class, Default.class, getManager().getRootContext()).compute(new RobustnessComputation(
                initialPoint,
                experiment.getFormula().getTimeNeeded(),
                experiment.getOdeSystem(),
                experiment.getPrecisionConfiguration(),
                experiment.getSimulationSpace(),
                experiment.getFormula())).full().get();
        VerificationResult simulationTime = getManager().resolve(ComputationContainer.class, Default.class, getManager().getRootContext()).compute(new RobustnessComputation(
                initialPoint,
                experiment.getSimulationSpace().getMaxBounds().getTime(),
                experiment.getOdeSystem(),
                experiment.getPrecisionConfiguration(),
                experiment.getSimulationSpace(),
                experiment.getFormula())).full().get();
        Assert.assertEquals(timeNeeded.getRobustness(0).getValue(), simulationTime.getRobustness(0).getValue(), 0.005f, "Analysis shouldn't be dependent on simulation length.");
    }

    protected final Experiment loadExperiment() throws IOException {
        return ExperimentImpl.fromPropertiesFile(TestLorenz84Oscilation.class.getClassLoader().getResource("org/sybila/parasim/application/ftest/lorenz84/experiment-oscil.properties").getFile());
    }

    protected static class RobustnessComputation extends AbstractComputation<VerificationResult> {

        private final float simulationTime;
        private final OrthogonalSpace originalSimulationSpace;
        private final Point initialPoint;

        @Provide
        private final OdeSystem odeSystem;
        @Provide
        private final PrecisionConfiguration precisionConfiguration;
        @Provide
        @SimulationSpace
        private final OrthogonalSpace simulationSpace;
        @Provide
        private final Formula property;
        @Inject
        private AdaptiveStepSimulator simulator;
        @Inject
        private AdaptiveStepConfiguration simulationConfiguration;
        @Inject
        private STLVerifier verifier;

        public RobustnessComputation(Point initialPoint, float simulationTime, OdeSystem odeSystem, PrecisionConfiguration precisionConfiguration, OrthogonalSpace simulationSpace, Formula property) {
            this.initialPoint = initialPoint;
            this.simulationTime = simulationTime;
            this.odeSystem = odeSystem;
            this.precisionConfiguration = precisionConfiguration;
            this.simulationSpace = new OrthogonalSpaceImpl(simulationSpace.getMinBounds(), new ArrayPoint(simulationTime, simulationSpace.getMaxBounds().toArray()), simulationSpace.getOdeSystem());
            this.originalSimulationSpace = simulationSpace;
            this.property = property;
        }

        @Override
        public Computation<VerificationResult> cloneComputation() {
            return new RobustnessComputation(initialPoint, simulationTime, odeSystem, precisionConfiguration, originalSimulationSpace, property);
        }

        @Override
        public VerificationResult call() throws Exception {
            final Trajectory trajectory = simulator.simulate(simulationConfiguration, new PointTrajectory(initialPoint));
            final Robustness robustness = verifier.verify(trajectory, property);
            return new AbstractVerificationResult() {

                @Override
                public int size() {
                    return 1;
                }

                @Override
                public Point getPoint(int index) {
                    return trajectory.getFirstPoint();
                }

                @Override
                public Robustness getRobustness(int index) {
                    return robustness;
                }
            };
        }

    }

}
