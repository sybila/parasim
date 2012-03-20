package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.computation.simulation.api.Status;
import org.sybila.parasim.computation.simulation.api.Configuration;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.Simulator;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.ode.ArrayOdeSystemEncoding;
import org.sybila.parasim.model.ode.DefaultOdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
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

    protected void testTimeStep(int size) {
        SimulatedDataBlock result = getSimulator().simulate(getConfiguration(), createDataBlock(getConfiguration().getDimension(), size));
        for (int s = 0; s < size; s++) {
            Point previous = null;
            for(Point p : result.getTrajectory(s)) {             
                if (previous == null) {
                    previous = p;
                    continue;
                }
                assertTrue(Math.abs(p.getTime() - previous.getTime()) <= getConfiguration().getTimeStep() + getConfiguration().getTimeStep() / 1000, "The time step condition doesn't hold, found time step <" + Math.abs(p.getTime() - previous.getTime()) + ">, expected time step <" + getConfiguration().getTimeStep() + ">");
                previous = p;
            }
        }
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
        return new ArrayDataBlock<Trajectory>(trajectories);
    }


    private OdeSystem createOdeSystem(final int dim) {
        int[] coefficientIndexes = new int[dim + 1];
        float[] coefficients = new float[dim];
        int[] factorIndexes = new int[dim + 1];
        int[] factors = new int[dim];
        for(int d = 0; d < dim; d++) {
            coefficientIndexes[d] = d;
            coefficients[d] = (float) dim / (float) 10;
            factorIndexes[d] = d;
            factors[d] = d;
        }
        coefficientIndexes[dim] = dim;
        factorIndexes[dim] = dim;
        return new DefaultOdeSystem(
            new ArrayOdeSystemEncoding(coefficientIndexes, coefficients, factorIndexes, factors)
        );
    }
    
    abstract protected Conf createConfiguration();
    
    abstract protected Simulator<Conf, Out> createSimulator(Conf configuaration);
    
}
