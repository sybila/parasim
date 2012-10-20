package org.sybila.parasim.application.model;

import java.io.File;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResultResource;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.xml.XMLRepresentable;
import org.sybila.parasim.model.xml.XMLResource;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class LoadedExperimentImpl implements LoadedExperiment {

    private final OdeSystem odeSystem;
    private final Formula formula;
    private final OrthogonalSpace initSpace;
    private final OrthogonalSpace simSpace;
    private final PrecisionConfiguration precision;
    private final InitialSampling sampling;
    private final long timeout;
    private final int iterations;
    private final XMLResource<VerificationResult> result;

    public LoadedExperimentImpl(OdeSystem odeSystem, Formula formula, OrthogonalSpace initialSpace, OrthogonalSpace simulationSpace, PrecisionConfiguration precisionConfiguration, InitialSampling initialSampling, long timeout, int iterationLimit, File resultFile) {
        this.odeSystem = odeSystem;
        this.formula = formula;
        this.initSpace = initialSpace;
        this.simSpace = simulationSpace;
        this.precision = precisionConfiguration;
        this.sampling = initialSampling;
        this.timeout = timeout;
        this.iterations = iterationLimit;
        result = new VerificationResultResource(resultFile);
    }

    private static <T extends XMLRepresentable> T checkAndGetRoot(XMLResource<T> resource, String description) {
        if (resource.getRoot() == null) {
            throw new IllegalArgumentException(description + " resource not loaded.");
        }
        return resource.getRoot();
    }

    public LoadedExperimentImpl(Experiment experiment) {
        odeSystem = experiment.getOdeSystem();
        formula = checkAndGetRoot(experiment.getSTLFormulaResource(), "Formula");
        initSpace = checkAndGetRoot(experiment.getInitialSpaceResource(), "Initial space");
        simSpace = checkAndGetRoot(experiment.getSimulationSpaceResource(), "Simulation space");
        precision = checkAndGetRoot(experiment.getPrecisionConfigurationResources(), "Precision configuration");
        sampling = checkAndGetRoot(experiment.getInitialSamplingResource(), "Initial sampling");
        timeout = experiment.getTimeoutInMilliSeconds();
        iterations = experiment.getIterationLimit();
        result = experiment.getVerificationResultResource();
    }

    @Override
    public Formula getFormula() {
        return formula;
    }

    @Override
    public InitialSampling getInitialSampling() {
        return sampling;
    }

    @Override
    public OrthogonalSpace getInitialSpace() {
        return initSpace;
    }

    @Override
    public int getIterationLimit() {
        return iterations;
    }

    @Override
    public OdeSystem getOdeSystem() {
        return odeSystem;
    }

    @Override
    public PrecisionConfiguration getPrecisionConfiguration() {
        return precision;
    }

    @Override
    public OrthogonalSpace getSimulationSpace() {
        return simSpace;
    }

    @Override
    public long getTimeoutInMilliSeconds() {
        return timeout;
    }

    @Override
    public XMLResource<VerificationResult> getVerificationResultResource() {
        return result;
    }
}
