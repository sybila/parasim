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
package org.sybila.parasim.computation.simulation;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.sybila.parasim.computation.simulation.api.Status;
import org.sybila.parasim.computation.simulation.api.Configuration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.Simulator;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.Times;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.ode.SimpleOdeSystem;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractSimulatorTest<Conf extends Configuration> {

    private Conf configuration;
    private Map<Integer, OdeSystem> odeSystems = new HashMap<>();
    private Map<OdeSystem, Conf> confs = new HashMap<>();
    private Simulator<Conf> simulator;

    protected Conf getConfiguration(OdeSystem odeSystem) {
        if (confs.get(odeSystem) == null) {
            confs.put(odeSystem, createConfiguration(odeSystem));
        }
        return confs.get(odeSystem);
    }

    protected Conf getConfiguration(int dimension) {
        return getConfiguration(getOdeSystem(dimension));
    }

    protected OdeSystem getOdeSystem(int dim) {
        if (odeSystems.get(dim) == null) {
            odeSystems.put(dim, createOdeSystem(dim));
        }
        return odeSystems.get(dim);
    }

    protected Simulator<Conf> getSimulator() {
        if (simulator == null) {
            simulator = createSimulator();
        }
        return simulator;
    }

    protected void testMinimalNumberOfPoints(int dimension, int size) throws Exception {
        SimulatedDataBlock result = getSimulator().simulate(getConfiguration(dimension), createDataBlock(dimension, size));
        for(int s = 0; s < size; s++) {
            for(Point p : result.getTrajectory(s)) {
            }
            assertTrue(result.getTrajectory(s).getLength() > 10, "The minimal number of point doesn't match. Expected at least <10>, was <" + result.getTrajectory(s).getLength() + ">.");
        }
    }

    protected void testValidNumberOfTrajectories(int dimension, int size) throws Exception {
        SimulatedDataBlock result = getSimulator().simulate(getConfiguration(dimension), createDataBlock(dimension, size));
        assertEquals(size, result.size());
        for(int s = 0; s < size; s++) {
            for(Point p : result.getTrajectory(s)) {
            }
            assertTrue(result.getTrajectory(s).getLength() > 0);
            assertEquals(result.getStatus(s), Status.OK);
        }
    }

    protected void testParameters(int dimension) throws Exception {
        float[] data = new float[dimension*2];
        for (int dim = 0; dim < dimension * 2; dim++) {
            data[dim] = dim;
        }
        Trajectory toSimulate = new PointTrajectory(0, data);
        SimulatedDataBlock result = getSimulator().simulate(getConfiguration(createParametrizedOdeSystem(dimension)), new ListDataBlock<>(Arrays.asList(toSimulate)));
        assertEquals(1, result.size());
        Trajectory resultTrajectory = result.getTrajectory(0);
        for (int dim = 1; dim < dimension; dim++) {
            assertEquals(data[dim - 1] + 1, resultTrajectory.getFirstPoint().getValue(dim), 0.01f);
        }
        for (int dim = dimension; dim < dimension; dim++) {
            assertEquals(data[dim], resultTrajectory.getFirstPoint().getValue(dim));
        }
    }

    protected DataBlock<Trajectory> createDataBlock(int dim, int size) {
        Trajectory[] trajectories = new Trajectory[size];
        for(int s = 0; s < size; s++) {
            float[] data = new float[dim];
            for (int d = 0; d < dim; d++) {
                data[d] = s * size + d;
            }
            trajectories[s] = new ArrayTrajectory(data, new float[] {(float) 0}, dim);
        }
        return new ArrayDataBlock<>(trajectories);
    }


    private OdeSystem createOdeSystem(final int dim) {
        final List<OdeSystemVariable> variables = new ArrayList<>();
        for (int d = 0; d < dim; d++) {
            variables.add(new OdeSystemVariable(new Variable("x"+d, d), new Times(new Constant((float) dim / (float) 10), new Variable("x"+d, d))));
        }
        return new SimpleOdeSystem(variables, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    private OdeSystem createParametrizedOdeSystem(final int dim) {
        final List<OdeSystemVariable> variables = new ArrayList<>();
        for (int d = 0; d < dim; d++) {
            variables.add(new OdeSystemVariable(new Variable("x"+d, d), new Parameter("p" + d, dim + d)));
        }
        return new SimpleOdeSystem(variables, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    abstract protected Conf createConfiguration(OdeSystem odeSystem);

    abstract protected Simulator<Conf> createSimulator();

}
