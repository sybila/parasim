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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.application.model;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import org.sybila.parasim.computation.density.api.ArrayInitialSampling;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.simulation.api.ArrayPrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.sbml.SBMLOdeSystemFactory;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FormulaResource;
import org.sybila.parasim.model.xml.XMLException;
import org.sybila.parasim.model.xml.XMLRepresentable;
import org.sybila.parasim.model.xml.XMLResource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestExperimentLauncher {

    private static final InitialSampling INITIAL_SAMPLING = new ArrayInitialSampling(10, 10);
    private static final OrthogonalSpace INITIAL_SPACE = new OrthogonalSpace(new ArrayPoint(0, 0, 0), new ArrayPoint(0, 20, 20));
    private static final PrecisionConfiguration PRECISION_CONFIGURATION = new ArrayPrecisionConfiguration(new float[]{0f, 0f}, 0.1f, 0.1f);
    private static final OrthogonalSpace SIMULATION_SPACE = new OrthogonalSpace(new ArrayPoint(0, 0, 0), new ArrayPoint(10, 200, 200));
    private static final String FORMULA_FILE = "formula.xml";
    private static final String ODE_FILE = "model.xml";

    private static class FailResultResource implements XMLResource<VerificationResult> {

        private static void fail(String methodName) {
            Assert.fail("VerificationResult resource should not be used during experiment launch (mehod +" + methodName + ").");
        }

        @Override
        public VerificationResult getRoot() {
            fail("getRoot");
            return null;
        }

        @Override
        public void setRoot(VerificationResult target) {
            fail("setRoot");
        }

        @Override
        public void load() throws XMLException {
            fail("load");
        }

        @Override
        public void store() throws XMLException {
            fail("store");
        }
    }

    private static class LoadingOnlyConstantXMLResource<T extends XMLRepresentable> implements XMLResource<T> {

        private boolean isLoaded;
        private T resource;

        public LoadingOnlyConstantXMLResource(T resource) {
            isLoaded = false;
            this.resource = resource;
        }

        @Override
        public T getRoot() {
            if (isLoaded) {
                return resource;
            } else {
                Assert.fail("Resource should be loaded before its root is used.");
                return null;
            }
        }

        @Override
        public void setRoot(T target) {
            Assert.fail("Root of resource should not be switched during experiment launch.");
        }

        @Override
        public void load() throws XMLException {
            isLoaded = true;
        }

        @Override
        public void store() throws XMLException {
            Assert.fail("Resource should not be stored during experiment launch.");
        }
    }

    private static class LoadingOnlyFormulaResource extends FormulaResource {

        private boolean isLoaded;

        public LoadingOnlyFormulaResource(File formula) {
            super(formula);
            isLoaded = false;
        }

        @Override
        public void load() throws XMLException {
            super.load();
            isLoaded = true;
        }

        @Override
        public Formula getRoot() {
            if (isLoaded) {
                return super.getRoot();
            } else {
                Assert.fail("Resource should be loaded before its root is used.");
                return null;
            }
        }

        @Override
        public void store() throws XMLException {
            Assert.fail("Resource should not be stored during experiment launch.");
        }
    }

    private static class TestExperiment implements Experiment {

        private FailResultResource resultResource;
        private LoadingOnlyConstantXMLResource<InitialSampling> initialSamplingResource;
        private LoadingOnlyConstantXMLResource<OrthogonalSpace> initialSpaceResource;
        private LoadingOnlyConstantXMLResource<PrecisionConfiguration> precisionSamplingResource;
        private LoadingOnlyConstantXMLResource<OrthogonalSpace> simulationSpaceResource;
        private LoadingOnlyFormulaResource formulaResource;
        private OdeSystem odeSystem;

        public TestExperiment() throws URISyntaxException, IOException {
            resultResource = new FailResultResource();
            initialSamplingResource = new LoadingOnlyConstantXMLResource<>(INITIAL_SAMPLING);
            initialSpaceResource = new LoadingOnlyConstantXMLResource<>(INITIAL_SPACE);
            precisionSamplingResource = new LoadingOnlyConstantXMLResource<>(PRECISION_CONFIGURATION);
            simulationSpaceResource = new LoadingOnlyConstantXMLResource<>(SIMULATION_SPACE);
            File formulaFile = new File(getClass().getClassLoader().getResource(FORMULA_FILE).toURI());
            formulaResource = new LoadingOnlyFormulaResource(formulaFile);
            File odeFile = new File(getClass().getClassLoader().getResource(ODE_FILE).toURI());
            odeSystem = SBMLOdeSystemFactory.fromFile(odeFile);
        }

        @Override
        public XMLResource<InitialSampling> getInitialSamplingResource() {
            return initialSamplingResource;
        }

        @Override
        public XMLResource<OrthogonalSpace> getInitialSpaceResource() {
            return initialSpaceResource;
        }

        @Override
        public int getIterationLimit() {
            return 10;
        }

        @Override
        public OdeSystem getOdeSystem() {
            return odeSystem;
        }

        @Override
        public XMLResource<PrecisionConfiguration> getPrecisionConfigurationResources() {
            return precisionSamplingResource;
        }

        @Override
        public FormulaResource getSTLFormulaResource() {
            return formulaResource;
        }

        @Override
        public XMLResource<OrthogonalSpace> getSimulationSpaceResource() {
            return simulationSpaceResource;
        }

        @Override
        public long getTimeoutInMilliSeconds() {
            return 480000;
        }

        @Override
        public XMLResource<VerificationResult> getVerificationResultResource() {
            return resultResource;
        }
    }
    private Manager manager;
    private Experiment experiment;

    @BeforeMethod
    public void startManager() throws Exception {
        manager = ManagerImpl.create();
        manager.start();
    }

    @BeforeMethod
    public void createExperiment() throws Exception {
        experiment = new TestExperiment();
    }

    @Test
    public void TestLaunch() throws Exception {
        VerificationResult result = ExperimentLauncher.launch(manager, experiment);

        /* TEST that container is correct -- how?? */
    }
}
