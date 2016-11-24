/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TestExperimentLauncher {
//
//    private static final float PRECISION = 0.1f; //precision setting for float comparison
//    private static final InitialSampling INITIAL_SAMPLING = new ArrayInitialSampling(10, 10);
//    private static final OrthogonalSpace INITIAL_SPACE = new OrthogonalSpace(new ArrayPoint(0, 0, 0), new ArrayPoint(0, 20, 20));
//    private static final PrecisionConfiguration PRECISION_CONFIGURATION = new ArrayPrecisionConfiguration(new float[]{0f, 0f}, 0.1f, 0.1f);
//    private static final OrthogonalSpace SIMULATION_SPACE = new OrthogonalSpace(new ArrayPoint(0, 0, 0), new ArrayPoint(10, 200, 200));
//    private static final String FORMULA_FILE = "formula.xml";
//    private static final String ODE_FILE = "model.xml";
//    private static final long TIMEOUT = 480000;
//    private static final int ITERATION_LIMIT = 10;
//    private static final float H_SAMPLING = getSampling(0);
//    private static final float V_SAMPLING = getSampling(1);
//
//    private static float getSampling(int dimension) {
//        return (INITIAL_SPACE.getMaxBounds().getValue(dimension) - INITIAL_SPACE.getMinBounds().getValue(dimension)) / (INITIAL_SAMPLING.getNumberOfSamples(dimension) - 1);
//    }
//
//    private static class FailResultResource implements XMLResource<VerificationResult> {
//
//        private static void fail(String methodName) {
//            Assert.fail("VerificationResult resource should not be used during experiment launch (mehod +" + methodName + ").");
//        }
//
//        @Override
//        public VerificationResult getRoot() {
//            fail("getRoot");
//            return null;
//        }
//
//        @Override
//        public void setRoot(VerificationResult target) {
//            fail("setRoot");
//        }
//
//        @Override
//        public void load() throws XMLException {
//            fail("load");
//        }
//
//        @Override
//        public void store() throws XMLException {
//            fail("store");
//        }
//    }
//
//    private static class LoadingOnlyConstantXMLResource<T extends XMLRepresentable> implements XMLResource<T> {
//
//        private boolean isLoaded, isUsed;
//        private T resource;
//
//        public LoadingOnlyConstantXMLResource(T resource) {
//            isLoaded = false;
//            isUsed = false;
//            this.resource = resource;
//        }
//
//        @Override
//        public T getRoot() {
//            if (isLoaded) {
//                isUsed = true;
//                return resource;
//            } else {
//                Assert.fail("Resource should be loaded before its root is used.");
//                return null;
//            }
//        }
//
//        @Override
//        public void setRoot(T target) {
//            Assert.fail("Root of resource should not be switched during experiment launch.");
//        }
//
//        @Override
//        public void load() throws XMLException {
//            isLoaded = true;
//        }
//
//        @Override
//        public void store() throws XMLException {
//            Assert.fail("Resource should not be stored during experiment launch.");
//        }
//
//        public boolean isUsed() {
//            return isUsed;
//        }
//    }
//
//    private static class LoadingOnlyFormulaResource extends FormulaResource {
//
//        private boolean isLoaded, isUsed;
//
//        public LoadingOnlyFormulaResource(File formula) {
//            super(formula);
//            isLoaded = false;
//            isUsed = false;
//        }
//
//        @Override
//        public void load() throws XMLException {
//            super.load();
//            isLoaded = true;
//        }
//
//        @Override
//        public Formula getRoot() {
//            if (isLoaded) {
//                isUsed = true;
//                return super.getRoot();
//            } else {
//                Assert.fail("Resource should be loaded before its root is used.");
//                return null;
//            }
//        }
//
//        @Override
//        public void store() throws XMLException {
//            Assert.fail("Resource should not be stored during experiment launch.");
//        }
//
//        public boolean isUsed() {
//            return isUsed;
//        }
//    }
//
//    private static class TestExperiment implements Experiment {
//
//        private FailResultResource resultResource;
//        private LoadingOnlyConstantXMLResource<InitialSampling> initialSamplingResource;
//        private LoadingOnlyConstantXMLResource<OrthogonalSpace> initialSpaceResource;
//        private LoadingOnlyConstantXMLResource<PrecisionConfiguration> precisionResource;
//        private LoadingOnlyConstantXMLResource<OrthogonalSpace> simulationSpaceResource;
//        private LoadingOnlyFormulaResource formulaResource;
//        private OdeSystem odeSystem;
//        private boolean odeSystemUsed, timeoutUsed, iterationLimitUsed;
//
//        public TestExperiment() throws URISyntaxException, IOException {
//            resultResource = new FailResultResource();
//            initialSamplingResource = new LoadingOnlyConstantXMLResource<>(INITIAL_SAMPLING);
//            initialSpaceResource = new LoadingOnlyConstantXMLResource<>(INITIAL_SPACE);
//            precisionResource = new LoadingOnlyConstantXMLResource<>(PRECISION_CONFIGURATION);
//            simulationSpaceResource = new LoadingOnlyConstantXMLResource<>(SIMULATION_SPACE);
//            File formulaFile = new File(getClass().getClassLoader().getResource(FORMULA_FILE).toURI());
//            formulaResource = new LoadingOnlyFormulaResource(formulaFile);
//            File odeFile = new File(getClass().getClassLoader().getResource(ODE_FILE).toURI());
//            odeSystem = SBMLOdeSystemFactory.fromFile(odeFile);
//            odeSystemUsed = false;
//            timeoutUsed = false;
//            iterationLimitUsed = false;
//        }
//
//        @Override
//        public XMLResource<InitialSampling> getInitialSamplingResource() {
//            return initialSamplingResource;
//        }
//
//        @Override
//        public XMLResource<OrthogonalSpace> getInitialSpaceResource() {
//            return initialSpaceResource;
//        }
//
//        @Override
//        public int getIterationLimit() {
//            iterationLimitUsed = true;
//            return ITERATION_LIMIT;
//        }
//
//        @Override
//        public OdeSystem getOdeSystem() {
//            odeSystemUsed = true;
//            return odeSystem;
//        }
//
//        @Override
//        public XMLResource<PrecisionConfiguration> getPrecisionConfigurationResources() {
//            return precisionResource;
//        }
//
//        @Override
//        public FormulaResource getSTLFormulaResource() {
//            return formulaResource;
//        }
//
//        @Override
//        public XMLResource<OrthogonalSpace> getSimulationSpaceResource() {
//            return simulationSpaceResource;
//        }
//
//        @Override
//        public long getTimeoutInMilliSeconds() {
//            timeoutUsed = true;
//            return TIMEOUT;
//        }
//
//        @Override
//        public XMLResource<VerificationResult> getVerificationResultResource() {
//            return resultResource;
//        }
//
//        public void assertAllResourcesUsed() {
//            if (!initialSamplingResource.isUsed()) {
//                fail("Initial sampling");
//            }
//            if (!initialSpaceResource.isUsed()) {
//                fail("Initial space");
//            }
//            if (!precisionResource.isUsed()) {
//                fail("Precision configuration");
//            }
//            if (!simulationSpaceResource.isUsed()) {
//                fail("Simulation space");
//            }
//            if (!formulaResource.isUsed()) {
//                fail("Formula");
//            }
//            if (!odeSystemUsed) {
//                fail("ODE system");
//            }
//            if (!timeoutUsed) {
//                fail("Timeout");
//            }
//            if (!iterationLimitUsed) {
//                fail("Iteration limit");
//            }
//        }
//
//        private void fail(String resource) {
//            Assert.fail(resource + " not loaded from the experiment.");
//        }
//    }
//    private Manager manager;
//    private TestExperiment experiment;
//
//    @BeforeMethod
//    public void startManager() throws Exception {
//        manager = ManagerImpl.create();
//        manager.start();
//    }
//
//    @BeforeMethod
//    public void createExperiment() throws Exception {
//        experiment = new TestExperiment();
//    }
//
//    @Test
//    public void TestLaunch() throws Exception {
//        VerificationResult result = ExperimentLauncher.launch(manager, experiment);
//        experiment.assertAllResourcesUsed();
//
//        for (int i = 0; i < result.size(); i++) {
//            Point target = result.getPoint(i);
//            float robustness = result.getRobustness(i).getValue();
//            assertPointHasCorrectDimensions(target);
//            if (!INITIAL_SPACE.isIn(target)) {
//                System.err.println(target);
//            }
//            // assertPointInInitialSpace(target);
//            assertCorrectRobustness(target, robustness);
//        }
//    }
//
//    private void assertPointHasCorrectDimensions(Point target) {
//        Assert.assertEquals(target.getDimension(), 2, "Point " + target.toString() + " has incorrect dimension.");
//    }
//
//    private void assertPointNearInitialSpace(Point target) {
//        if (!INITIAL_SPACE.isIn(target)) {
//            float hDist = getDistance(target, 0);
//            float vDist = getDistance(target, 1);
//            if (hDist > H_SAMPLING / 2) {
//                Assert.fail("Point " + target.toString() + " is too far from the initial space.");
//            }
//            if (vDist > V_SAMPLING / 2) {
//                Assert.fail("Point " + target.toString() + " is too far from the initial space.");
//            }
//        }
//    }
//
//    private float getDistance(Point target, int dimension) {
//        return Math.max(INITIAL_SPACE.getMinBounds().getValue(dimension) - target.getValue(dimension),
//                target.getValue(dimension) - INITIAL_SPACE.getMaxBounds().getValue(dimension));
//    }
//
//    private void assertCorrectRobustness(Point target, float robustness) {
//        Assert.assertEquals(robustness, target.getValue(0) + target.getValue(1) - 30, PRECISION, "Point " + target.toString() + " has incorrect robustness value.");
//    }
}
