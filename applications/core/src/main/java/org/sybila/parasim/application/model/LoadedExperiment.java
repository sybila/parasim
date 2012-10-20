package org.sybila.parasim.application.model;

import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.xml.XMLResource;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface LoadedExperiment {

    public OdeSystem getOdeSystem();

    public Formula getFormula();

    public OrthogonalSpace getInitialSpace();

    public OrthogonalSpace getSimulationSpace();

    public PrecisionConfiguration getPrecisionConfiguration();

    public InitialSampling getInitialSampling();

    public int getIterationLimit();

    public long getTimeoutInMilliSeconds();

    public XMLResource<VerificationResult> getVerificationResultResource();
}
