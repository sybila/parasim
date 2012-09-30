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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.sybila.parasim.computation.density.api.InitialSampling;
import org.sybila.parasim.computation.lifecycle.api.ComputationContainer;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.annotations.Default;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.ode.SimpleOdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.model.verification.stl.FutureFormula;
import org.sybila.parasim.model.verification.stl.IntervalBoundaryType;
import org.sybila.parasim.model.verification.stl.LinearPredicate;
import org.sybila.parasim.model.verification.stl.TimeInterval;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestValidityRegionsComputation {

    private Manager manager;

    @BeforeMethod
    public void startManager() throws Exception {
        manager = ManagerImpl.create();
        manager.start();
    }

    public void stopManager() {
        manager.shutdown();
    }

    @Test
    public void testComputation() throws ExecutionException, InterruptedException {
        ValidityRegionsComputation computation = new ValidityRegionsComputation(createOdeSystem(), createPrecisionConfiguration(), createInitialSampling(), createSimulationSpace(), createInitialSpace(), createFutureFormula(-1f), 0);
        ComputationContainer container = manager.resolve(ComputationContainer.class, Default.class, manager.getRootContext());
        VerificationResult result = container.compute(computation).full().get();
        for (int i=0; i<result.size(); i++) {
            assertTrue(result.getRobustness(i).getValue() > 0, "The robustness in index [" + i + "] (point " + result.getPoint(i) + ") should be poisitive, but was " + result.getRobustness(i).getValue());
            assertEquals(result.getPoint(i).getTime(), 0f);
        }
    }

    // TODO: fix test
    @Test(enabled=false)
    public void testComputation2() throws ExecutionException, InterruptedException {
        ValidityRegionsComputation computation = new ValidityRegionsComputation(createOdeSystem(), createPrecisionConfiguration(), createInitialSampling(), createSimulationSpace(), createInitialSpace(), createFutureFormula(2), 0);
        ComputationContainer container = manager.resolve(ComputationContainer.class, Default.class, manager.getRootContext());
        VerificationResult result = container.compute(computation).full().get();
        for (int i=0; i<result.size(); i++) {
            assertEquals(result.getPoint(i).getTime(), 0f);
            if (result.getPoint(i).getValue(0) > 2) {
                assertTrue(result.getRobustness(i).getValue() > 0, "The robustness in index [" + i + "] (point " + result.getPoint(i) + ") should be poisitive, but was " + result.getRobustness(i).getValue());
            } else {
                assertTrue(result.getRobustness(i).getValue() <= 0, "The robustness in index [" + i + "] (point " + result.getPoint(i) + ") should be negative, but was " + result.getRobustness(i).getValue());
            }
        }
    }

    private Formula createFutureFormula(float constant) {
        Map<Integer, Float> multipliers = new HashMap<>();
        multipliers.put(0, 1f);
        return new FutureFormula(
                new LinearPredicate(multipliers, constant, LinearPredicate.Type.GREATER,
                    new PointVariableMapping() {
                        public int getDimension() {
                            return 1;
                        }
                        public Integer getKey(String variableName) {
                            return 0;
                        }
                        public String getName(Integer variableKey) {
                            return "x";
                        }
                    }),
                new TimeInterval(0.5f, 1f, IntervalBoundaryType.CLOSED));
    }

    private InitialSampling createInitialSampling() {
        return new InitialSampling() {
            @Override
            public int getDimension() {
                return 1;
            }
            @Override
            public int getNumberOfSamples(int dim) {
                return 30;
            }
            @Override
            public Element toXML(Document doc) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            @Override
            public OdeSystem getOdeSystem() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    private OrthogonalSpaceImpl createInitialSpace() {
        return new OrthogonalSpaceImpl(new ArrayPoint(0, 0), new ArrayPoint(0, 10), createOdeSystem());
    }

    private OdeSystem createOdeSystem() {
        final List<OdeSystemVariable> variables = new ArrayList<>();
        variables.add(new OdeSystemVariable(new Variable("x", 0), new Variable("x", 0)));
        return new SimpleOdeSystem(variables, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    private PrecisionConfiguration createPrecisionConfiguration() {
        return new PrecisionConfiguration() {
            public int getDimension() {
                return 1;
            }
            public float getMaxAbsoluteError(int dim) {
                return 0;
            }
            public float getMaxRelativeError() {
                return 0;
            }
            public Element toXML(Document doc) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public float getTimeStep() {
                return 0.01f;
            }
        };
    }

    private OrthogonalSpaceImpl createSimulationSpace() {
        return new OrthogonalSpaceImpl(new ArrayPoint(0, 0), new ArrayPoint(1.5f, 10), createOdeSystem());
    }

}
