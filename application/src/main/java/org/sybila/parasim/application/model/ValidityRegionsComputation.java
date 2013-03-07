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
package org.sybila.parasim.application.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.density.api.annotations.InitialSpace;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlockWrapper;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.computation.lifecycle.api.annotations.RunWith;
import org.sybila.parasim.computation.lifecycle.impl.common.ComputationLifecycleConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.annotations.SimulationSpace;
import org.sybila.parasim.computation.verification.api.STLVerifier;
import org.sybila.parasim.computation.verification.api.VerifiedDataBlock;
import org.sybila.parasim.computation.verification.api.VerifiedDataBlockResultAdapter;
import org.sybila.parasim.core.annotation.Inject;
import org.sybila.parasim.core.annotation.Provide;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@RunWith(balancer=ValidityRegionsIterationBalancer.class, offerer=ValidityRegionsIterationOfferer.class)
public class ValidityRegionsComputation implements Computation<VerificationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidityRegionsComputation.class);

    @Provide
    private final OdeSystem odeSystem;
    @Provide
    private final PrecisionConfiguration precisionConfiguration;
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
    private Emitter emitter;
    @Inject
    private ComputationLifecycleConfiguration computationLifecycleConfiguration;


    private int iterationLimit;
    private int currentIteration = 0;
    private SpawnedDataBlock spawned;
    private final OrthogonalSpace originalSimulationSpace;

    public ValidityRegionsComputation(OdeSystem odeSystem, PrecisionConfiguration precisionConfiguration, OrthogonalSpace simulationSpace, OrthogonalSpace initialSpace, Formula property, int iterationLimit) {
        if (odeSystem == null) {
            throw new IllegalArgumentException("The parameter [odeSystem] is null.");
        }
        if (precisionConfiguration == null) {
            throw new IllegalArgumentException("The parameter [precisionConfiguration] is null.");
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
        this.simulationSpace = new OrthogonalSpaceImpl(simulationSpace.getMinBounds(), new ArrayPoint(property.getTimeNeeded(), simulationSpace.getMaxBounds().toArray()), simulationSpace.getOdeSystem());
        this.originalSimulationSpace = simulationSpace;
        this.initialSpace = initialSpace;
        this.property = property;
        this.iterationLimit = iterationLimit;
    }

    @Override
    public VerificationResult call() throws Exception {
        if (spawned == null) {
            spawned = emit(spawner.spawn(initialSpace), true);
        }
        currentIteration++;
        LOGGER.info("iteration <" + currentIteration + "> started with <" + spawned.size() + "> spawned primary and <" + spawned.getSecondaryTrajectories().size() + "> secondary trajectories.");
        SimulatedDataBlock<TrajectoryWithNeighborhood> simulated = simulator.simulate(simulationConfiguration, spawned);
        SimulatedDataBlock simulatedSecondary = null;
        if (spawned.getSecondaryTrajectories().size() > 0) {
            simulatedSecondary = simulator.simulate(simulationConfiguration, spawned.getSecondaryTrajectories());
        }
        VerifiedDataBlock<TrajectoryWithNeighborhood> verified = verifier.verify(simulated, property);
        VerificationResult result = new VerifiedDataBlockResultAdapter(verified);
        if (iterationLimit != 0 && currentIteration == iterationLimit) {
            if (simulatedSecondary != null) {
                VerifiedDataBlock<Trajectory> verifiedSecondary = verifier.verify(simulatedSecondary, property);
                result = result.merge(new VerifiedDataBlockResultAdapter(verifiedSecondary));
            }
            LOGGER.warn("iteration limit <" + iterationLimit + "> reached");
        } else {
            DistanceCheckedDataBlock distanceChecked = distanceChecker.check(spawned.getConfiguration(), verified);
            if (simulatedSecondary != null) {
                VerifiedDataBlock<Trajectory> verifiedSecondary = verifier.verify(simulatedSecondary, property);
                result = result.merge(new VerifiedDataBlockResultAdapter(verifiedSecondary));
            }
            emit(spawner.spawn(spawned.getConfiguration(), distanceChecked), false);
        }
        return result;
    }

    public int getCurrentIteration() {
        return currentIteration;
    }

    protected SpawnedDataBlock emit(SpawnedDataBlock spawned, boolean returnOneInsteadOfEmitting) {
        if (spawned.size() == 0) {
            return null;
        }
        SpawnedDataBlock result = null;
        Set<Trajectory> originalSecondaryTrajectories = new HashSet<>(spawned.getSecondaryTrajectories().size());
        for (Trajectory t: spawned.getSecondaryTrajectories()) {
            originalSecondaryTrajectories.add(t);
        }
        int toSpawn = (int) Math.min(Math.ceil(spawned.size() / (float) 30), computationLifecycleConfiguration.getCorePoolSize());
        int batchSize = (int) Math.ceil(spawned.size() / (float) toSpawn);
        for (int i=0; i<toSpawn; i++) {
            int batchStart = batchSize * i;
            int batchEnd = Math.min(batchSize * (i + 1), spawned.size());
            List<TrajectoryWithNeighborhood> localSpawned = new ArrayList<>(batchSize);
            List<Trajectory> localSecondarySpawned = new ArrayList<>();
            Set<Trajectory> localSecondarySpawnedCache = new HashSet<>();
            for (int j=batchStart; j<batchEnd; j++) {
                localSpawned.add(spawned.getTrajectory(j));
                for (Trajectory secondary: spawned.getTrajectory(j).getNeighbors()) {
                    if (originalSecondaryTrajectories.contains(secondary) && !localSecondarySpawnedCache.contains(secondary)) {
                        localSecondarySpawned.add(secondary);
                        localSecondarySpawnedCache.add(secondary);
                    }
                }
            }
            if (returnOneInsteadOfEmitting && result == null) {
                result = new SpawnedDataBlockWrapper(new ListDataBlock<>(localSpawned), spawned.getConfiguration(), new ListDataBlock<>(localSecondarySpawned));
            } else {
                ValidityRegionsComputation computation = (ValidityRegionsComputation) cloneComputation();
                computation.spawned = new SpawnedDataBlockWrapper(new ListDataBlock<>(localSpawned), spawned.getConfiguration(), new ListDataBlock<>(localSecondarySpawned));
                emitter.emit(computation);
            }
        }
        return result;
    }

    public Computation<VerificationResult> cloneComputation() {
        ValidityRegionsComputation computation = new ValidityRegionsComputation(odeSystem, precisionConfiguration, originalSimulationSpace, initialSpace, property, iterationLimit);
        computation.currentIteration = this.currentIteration;
        computation.spawned = this.spawned;
        return computation;
    }

    @Override
    public void destroy() throws Exception {
    }
}
