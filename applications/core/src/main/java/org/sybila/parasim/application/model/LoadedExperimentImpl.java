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
