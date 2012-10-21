package org.sybila.parasim.computation.simulation.octave;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.type.OctaveDouble;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.ode.OctaveOdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class OctaveSimulationEngine implements SimulationEngine {

    private final OctaveEngine octave;
    private final long stepLimit;
    private final double  relativeTolerance;

    public OctaveSimulationEngine(OctaveEngine octave, long stepLimit, double relativeTolerance) {
        Validate.notNull(octave);
        Validate.isTrue(stepLimit > 0);
        this.octave = octave;
        this.relativeTolerance = relativeTolerance;
        this.stepLimit = stepLimit;
    }

    @Override
    public void close() {
        getOctave().close();
    }

    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeStep, double timeLimit) {
        // load parameter values
        List<ParameterValue> paramValues = loadParameterValues(point, odeSystem);
        // create substituted octave ode system
        OctaveOdeSystem octaveOdeSystem = paramValues.isEmpty() ? new OctaveOdeSystem(odeSystem) : new OctaveOdeSystem(odeSystem.substitute(paramValues));
        // compute
        long numOfIterations = Math.min(Math.round((timeLimit - point.getTime()) / timeStep), getStepLimit());
        double[] loadedData = rawSimulation(point, octaveOdeSystem, numOfIterations, timeStep).getData();
        float[] data = new float[loadedData.length];
        for (int dim = 0; dim < octaveOdeSystem.dimension(); dim++) {
            for (int i = 0; i < loadedData.length / octaveOdeSystem.dimension(); i++) {
                data[dim + i * octaveOdeSystem.dimension()] = (float) loadedData[dim * (loadedData.length / octaveOdeSystem.dimension()) + i];
            }
        }
        float[] times = new float[loadedData.length / octaveOdeSystem.dimension()];
        float time = point.getTime();
        for (int i = 0; i < times.length; i++) {
            time += timeStep;
            times[i] = time;
        }
        if (paramValues.isEmpty()) {
            return new ArrayTrajectory(data, times, point.getDimension());
        } else {
            return new ArrayTrajectory(point, data, times, octaveOdeSystem.dimension());
        }
    }

    protected final OctaveEngine getOctave() {
        return octave;
    }

    protected final double getRelativeTolerance() {
        return relativeTolerance;
    }

    protected final long getStepLimit() {
        return stepLimit;
    }

    private List<ParameterValue> loadParameterValues(Point point, OdeSystem odeSystem) {
        List<ParameterValue> paramValues = new ArrayList<>();
        for (Parameter param: odeSystem.getAvailableParameters().values()) {
            if (!param.isSubstituted()) {
                paramValues.add(new ParameterValue(param, point.getValue(param.getIndex())));
            }
        }
        return paramValues;
    }

    protected abstract OctaveDouble rawSimulation(Point point, OctaveOdeSystem odeSystem, long numberOfIterations, double timeStep);

}
