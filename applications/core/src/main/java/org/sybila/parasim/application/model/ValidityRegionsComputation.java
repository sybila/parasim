package org.sybila.parasim.application.model;

import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.density.api.LimitedDistance;
import org.sybila.parasim.computation.density.api.PointDistanceMetric;
import org.sybila.parasim.computation.density.api.annotations.InitialSpace;
import org.sybila.parasim.computation.density.spawn.api.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.annotations.SimulationSpace;
import org.sybila.parasim.computation.verification.api.STLVerifier;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.result.VerifiedDataBlock;
import org.sybila.parasim.model.verification.result.VerifiedDataBlockResultAdapter;
import org.sybila.parasim.model.verification.stl.Formula;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ValidityRegionsComputation extends AbstractComputation<VerificationResult> {

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
    @Provide
    private final PointDistanceMetric pointDistanceMetric;
    @Inject
    private AdaptiveStepSimulator simulator;
    @Inject
    private AdaptiveStepConfiguration simulationConfiguration;
    @Inject
    private TrajectorySpawner spawner;
    @Inject
    private STLVerifier verifier;

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
        this.pointDistanceMetric = new PointDistanceMetric() {

            public LimitedDistance distance(float[] first, float[] second) {
                return null;
            }

            public LimitedDistance distance(Point first, Point second) {
                return null;
            }
        };
    }

    public VerificationResult compute() {
        SpawnedDataBlock spawned = spawner.spawn(initialSpace, initialSampling, pointDistanceMetric);
        SimulatedDataBlock simulated = simulator.simulate(simulationConfiguration, spawned);
        VerifiedDataBlock verified = verifier.verify(simulated, property);
        return new VerifiedDataBlockResultAdapter(verified);
    }

    public Computation<VerificationResult> cloneComputation() {
        return new ValidityRegionsComputation(odeSystem, precisionConfiguration, initialSampling, simulationSpace, initialSpace, property);
    }
}
