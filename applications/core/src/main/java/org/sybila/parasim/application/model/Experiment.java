package org.sybila.parasim.application.model;

import org.sybila.parasim.computation.density.api.InitialSamplingResource;
import org.sybila.parasim.computation.simulation.api.PrecisionConfigurationResource;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpaceResource;
import org.sybila.parasim.model.verification.result.VerificationResultResource;
import org.sybila.parasim.model.verification.stl.FormulaResource;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Experiment {

    OdeSystem getOdeSystem();

    FormulaResource getSTLFormulaResource();

    OrthogonalSpaceResource getInitialSpaceResource();

    OrthogonalSpaceResource getSimulationSpaceResource();

    PrecisionConfigurationResource getPrecisionConfigurationResources();

    InitialSamplingResource getInitialSamplingResource();

    long getTimeoutInMilliSeconds();

    VerificationResultResource getVerificationResultResource();
}
