package org.sybila.parasim.application.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.api.annotations.InitialSpace;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
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
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.computation.ComputationFailedException;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.LinkedTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ValidityRegionsComputation extends AbstractComputation<VerificationResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidityRegionsComputation.class);
    private static final int ITERATIONS = 3; // FIXME: hardcoded

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

    public ValidityRegionsComputation(OdeSystem odeSystem, PrecisionConfiguration precisionConfiguration, InitialSampling initialSampling, OrthogonalSpace simulationSpace, OrthogonalSpace initialSpace, Formula property) {
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
    }

    @Override
    public VerificationResult compute() throws ComputationFailedException {
        SpawnedDataBlock spawned = spawner.spawn(initialSpace, initialSampling);
        VerificationResult result = null;
        int iteration = 0;
        while (spawned.size() != 0) {
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
            DistanceCheckedDataBlock distanceChecked = distanceChecker.check(spawned.getConfiguration(), verified);
            spawned = spawner.spawn(spawned.getConfiguration(), distanceChecked);
            iteration++;
        }
        if (LOGGER.isDebugEnabled()) {
            for (int i=0; i<result.size(); i++) {
                LOGGER.debug("robustness for " + result.getPoint(i) + " is " + result.getRobustness(i).getValue());
            }
        }
        return result;
    }

    public Computation<VerificationResult> cloneComputation() {
        return new ValidityRegionsComputation(odeSystem, precisionConfiguration, initialSampling, simulationSpace, initialSpace, property);
    }
}
