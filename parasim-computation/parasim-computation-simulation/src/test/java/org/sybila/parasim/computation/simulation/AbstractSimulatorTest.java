package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import java.util.HashMap;
import java.util.Map;
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
    
    protected void testValidNumberOfTrajectories(int size) {
        SimulatedDataBlock<Trajectory> result = getSimulator().simulate(getConfiguration(), createDataBlock(getConfiguration().getDimension(), size));
        assertEquals(size, result.size());
        for(int s = 0; s < size; s++) {
            for(Point p : result.getTrajectory(s)) {             
            }
            assertTrue(result.getTrajectory(s).getLength() > 0);
            assertEquals(Status.TIMEOUT, result.getStatus(s));
        }
    }
  
    private ArrayDataBlock<Trajectory> createDataBlock(int dim, int size) {
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
        return new OdeSystem() {

            @Override
            public float value(Point point, int dimension) {
                return value(point.toArray(), dimension);
            }

            @Override
            public float value(float[] point, int dimension) {
                if (dimension < 0 || dimension > dim) {
                    throw new IndexOutOfBoundsException("The specified dimension is out of the range [0," + dim + "].");
                }
                return ((float)dimension)/(float)100;
            }

            @Override
            public int dimension() {
                return dim;
            }

            public String octaveName() {
                return "f";
            }

            public String octaveString() {
                StringBuilder result = new StringBuilder();
                result.append("function xdot = f(x, t) ");
                result.append("xdot = zeros(").append(dim).append(", 1);");
                for(int dim=0; dim<dimension(); dim++) {
                    result.append("xdot(").append(dim+1).append(") = ").append(((float)dim)/(float)100).append(";");
                }
                result.append("endfunction");
                return result.toString();
            }
        };
    }
    
    abstract protected Conf createConfiguration();
    
    abstract protected Simulator<Conf, Out> createSimulator(Conf configuaration);
    
}
