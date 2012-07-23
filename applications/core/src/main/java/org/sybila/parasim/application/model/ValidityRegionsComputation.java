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

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.api.annotations.InitialSpace;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlockWrapper;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.computation.lifecycle.api.annotations.RunWith;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.annotations.SimulationSpace;
import org.sybila.parasim.computation.verification.api.STLVerifier;
import org.sybila.parasim.computation.verification.api.VerifiedDataBlock;
import org.sybila.parasim.computation.verification.api.VerifiedDataBlockResultAdapter;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.execution.api.SharedMemoryExecutor;
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.computation.ThreadId;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.LinkedTrajectory;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@RunWith(executor=SharedMemoryExecutor.class)
public class ValidityRegionsComputation extends AbstractComputation<VerificationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidityRegionsComputation.class);

    @Provide
    private final OdeSystem odeSystem;
    @Provide
    private final PrecisionConfiguration precisionConfiguration;
    @Provide
    private final InitialSampling initialSampling;
    @Provide
    @SimulationSpace
    private final OrthogonalSpace simulationSpace;
    @Provide
    @InitialSpace
    private final OrthogonalSpace initialSpace;
    @Provide
    private final Formula property;
    @Inject
    private AdaptiveStepSimulator simulator;
    @Inject
    private AdaptiveStepConfiguration simulationConfiguration;
    @Inject
    private TrajectorySpawner spawner;
    @Inject
    private STLVerifier verifier;
    @Inject
    private DistanceChecker distanceChecker;

    @Inject
    private ThreadId threadId;

    private int iterationLimit;

    public ValidityRegionsComputation(OdeSystem odeSystem, PrecisionConfiguration precisionConfiguration, InitialSampling initialSampling, OrthogonalSpace simulationSpace, OrthogonalSpace initialSpace, Formula property, int iterationLimit) {
        if (odeSystem == null) {
            throw new IllegalArgumentException("The parameter [odeSystem] is null.");
        }
        if (precisionConfiguration == null) {
            throw new IllegalArgumentException("The parameter [precisionConfiguration] is null.");
        }
        if (initialSampling == null) {
            throw new IllegalArgumentException("The parameter [initialSampling] is null.");
        }
        if (simulationSpace == null) {
            throw new IllegalArgumentException("The parameter [simulationSpace] is null.");
        }
        if (initialSpace == null) {
            throw new IllegalArgumentException("The parameter [initialSpace] is null.");
        }
        if (property == null) {
            throw new IllegalArgumentException("The parameter [property] is null.");
        }
        this.odeSystem = odeSystem;
        this.precisionConfiguration = precisionConfiguration;
        this.initialSampling = initialSampling;
        this.simulationSpace = simulationSpace;
        this.initialSpace = initialSpace;
        this.property = property;
        this.iterationLimit = iterationLimit;
    }

    @Override
    public VerificationResult call() throws Exception {
        SpawnedDataBlock spawned = spawner.spawn(initialSpace, initialSampling);
        int batchSize = (int) Math.ceil(spawned.size() / (float) (threadId.maxId() + 1));
        int batchStart = batchSize * threadId.currentId();
        int batchEnd = Math.min(batchSize * (threadId.currentId() + 1), spawned.size());
        List<Trajectory> localSpawned = new ArrayList<>(batchSize);
        List<Trajectory> localSecondarySpawned = new ArrayList<>();
        for (int i=batchStart; i<batchEnd; i++) {
            localSpawned.add(spawned.getTrajectory(i));
            for (Trajectory secondary: spawned.getConfiguration().getNeighborhood().getNeighbors(spawned.getTrajectory(i))) {
                localSecondarySpawned.add(secondary);
            }
        }
        spawned = new SpawnedDataBlockWrapper(new ListDataBlock<>(localSpawned), spawned.getConfiguration(), new ListDataBlock<>(localSecondarySpawned));
        VerificationResult result = null;
        int iteration = 0;
        while (spawned.size() != 0) {
            iteration++;
            LOGGER.info("["+threadId.currentId()+"] iteration <" + iteration + "> started with <" + spawned.size() + "> spawned trajectories.");
            SimulatedDataBlock simulated = simulator.simulate(simulationConfiguration, spawned);
            for (int i=0; i<spawned.size(); i++) {
                LinkedTrajectory.createAndUpdateReference(spawned.getTrajectory(i)).append(simulated.getTrajectory(i));
            }
            if (spawned.getSecondaryTrajectories().size() > 0) {
                SimulatedDataBlock simulatedSecondary = simulator.simulate(simulationConfiguration, spawned.getSecondaryTrajectories());
                for (int i=0; i<spawned.getSecondaryTrajectories().size(); i++) {
                    LinkedTrajectory.createAndUpdateReference(spawned.getSecondaryTrajectories().getTrajectory(i)).append(simulatedSecondary.getTrajectory(i));
                }
            }
            VerifiedDataBlock<Trajectory> verified = verifier.verify(simulated, property);
            if (result == null) {
                result = new VerifiedDataBlockResultAdapter(verified);
            } else {
                result = result.merge(new VerifiedDataBlockResultAdapter(verified));
            }
            if (iterationLimit != 0 && iteration == iterationLimit) {
                LOGGER.warn("["+threadId.currentId()+"] iteration limit <" + iterationLimit + "> reached");
                break;
            }
            DistanceCheckedDataBlock distanceChecked = distanceChecker.check(spawned.getConfiguration(), verified);
            spawned = spawner.spawn(spawned.getConfiguration(), distanceChecked);
        }
        return result;
    }

    public Computation<VerificationResult> cloneComputation() {
        return new ValidityRegionsComputation(odeSystem, precisionConfiguration, initialSampling, simulationSpace, initialSpace, property, iterationLimit);
    }
}
