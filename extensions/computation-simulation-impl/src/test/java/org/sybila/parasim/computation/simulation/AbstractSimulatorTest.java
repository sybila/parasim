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
package org.sybila.parasim.computation.simulation;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.sybila.parasim.computation.simulation.api.Status;
import org.sybila.parasim.computation.simulation.api.Configuration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.Simulator;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.math.Times;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import static org.testng.Assert.*;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractSimulatorTest<Conf extends Configuration, Out extends SimulatedDataBlock> {

    private Conf configuration;
    private Map<Integer, OdeSystem> odeSystems = new HashMap<Integer, OdeSystem>();
    private Simulator<Conf, Out> simulator;

    protected Conf getConfiguration() {
        if (configuration == null) {
            configuration = createConfiguration();
        }
        return configuration;
    }

    protected OdeSystem getOdeSystem(int dim) {
        if (odeSystems.get(dim) == null) {
            odeSystems.put(dim, createOdeSystem(dim));
        }
        return odeSystems.get(dim);
    }

    protected Simulator<Conf, Out> getSimulator() {
        if (simulator == null) {
            simulator = createSimulator(getConfiguration());
        }
        return simulator;
    }

    protected void testMinimalNumberOfPoints(int size) {
        SimulatedDataBlock result = getSimulator().simulate(getConfiguration(), createDataBlock(getConfiguration().getDimension(), size));
        for(int s = 0; s < size; s++) {
            for(Point p : result.getTrajectory(s)) {
            }
            assertTrue(result.getTrajectory(s).getLength() > 10, "The minimal number of point doesn't match.");
        }
    }

    protected void testValidNumberOfTrajectories(int size) {
        SimulatedDataBlock result = getSimulator().simulate(getConfiguration(), createDataBlock(getConfiguration().getDimension(), size));
        assertEquals(size, result.size());
        for(int s = 0; s < size; s++) {
            for(Point p : result.getTrajectory(s)) {
            }
            assertTrue(result.getTrajectory(s).getLength() > 0);
            assertEquals(Status.TIMEOUT, result.getStatus(s));
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
        return new OdeSystem() {
            @Override
            public int dimension() {
                return variables.size();
            }

            @Override
            public OdeSystemVariable getVariable(int dimension) {
                return variables.get(dimension);
            }

            @Override
            public Iterator<OdeSystemVariable> iterator() {
                return variables.iterator();
            }
        };
    }

    abstract protected Conf createConfiguration();

    abstract protected Simulator<Conf, Out> createSimulator(Conf configuaration);

}
