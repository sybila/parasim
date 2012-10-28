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
package org.sybila.parasim.computation.simulation.octave;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.type.OctaveDouble;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OctaveOdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class OctaveSimulationEngine implements SimulationEngine {

    protected static final Logger LOGGER = LoggerFactory.getLogger(OctaveSimulationEngine.class);

    private final OctaveEngine octave;
    private final long stepLimit;

    public OctaveSimulationEngine(OctaveEngine octave, long stepLimit) {
        Validate.notNull(octave);
        Validate.isTrue(stepLimit > 0);
        this.octave = octave;
        this.stepLimit = stepLimit;
    }

    @Override
    public void close() {
        getOctave().close();
    }

    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration precision) {
        // load parameter values
        List<ParameterValue> paramValues = loadParameterValues(point, odeSystem);
        // create substituted octave ode system
        OctaveOdeSystem octaveOdeSystem = paramValues.isEmpty() ? new OctaveOdeSystem(odeSystem) : new OctaveOdeSystem(odeSystem.substitute(paramValues));
        // compute
        long numOfIterations = Math.min(Math.round((timeLimit - point.getTime()) / precision.getTimeStep()), getStepLimit());
        double[] loadedData = rawSimulation(point, octaveOdeSystem, numOfIterations, precision).getData();
        float[] data = new float[loadedData.length];
        for (int dim = 0; dim < octaveOdeSystem.dimension(); dim++) {
            for (int i = 0; i < loadedData.length / octaveOdeSystem.dimension(); i++) {
                data[dim + i * octaveOdeSystem.dimension()] = (float) loadedData[dim * (loadedData.length / octaveOdeSystem.dimension()) + i];
            }
        }
        float[] times = new float[loadedData.length / octaveOdeSystem.dimension()];
        float time = point.getTime();
        for (int i = 0; i < times.length; i++) {
            time += precision.getTimeStep();
            times[i] = time;
        }
        if (paramValues.isEmpty()) {
            return new ArrayTrajectory(data, times, point.getDimension());
        } else {
            return new ArrayTrajectory(point, data, times, octaveOdeSystem.dimension());
        }
    }

    public Trajectory simulateAndPlot(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration precision) {
        Trajectory trajectory = simulate(point, odeSystem, timeLimit, precision);
        boolean first = true;
        StringBuilder timeBuilder = new StringBuilder().append("time = [");
        for (Point p: trajectory) {
            if (first) {
                first = false;
            } else {
                timeBuilder.append(", ");
            }
            timeBuilder.append(p.getTime());
        }
        timeBuilder.append("];");
        getOctave().eval(timeBuilder.toString());
        getOctave().eval("plot(time, y);");
        StringBuilder legendBuilder = new StringBuilder().append("legend(");
        first = true;
        for (Variable var: odeSystem) {
            if (first) {
                first = false;
            } else {
                legendBuilder.append(", ");
            }
            legendBuilder.append("'").append(var.getName()).append("'");
        }
        legendBuilder.append(");");
        getOctave().eval(legendBuilder.toString());
        getOctave().eval("drawnow();");
        return trajectory;
    }

    protected final OctaveEngine getOctave() {
        return octave;
    }

    protected final long getStepLimit() {
        return stepLimit;
    }

    protected static boolean absoluteToleranceIsSet(PrecisionConfiguration precision) {
        for (int i=0; i<precision.getDimension(); i++) {
            if (precision.getMaxAbsoluteError(i) != 0) {
                return true;
            }
        }
        return false;
    }

    private List<ParameterValue> loadParameterValues(Point point, OdeSystem odeSystem) {
        List<ParameterValue> paramValues = new ArrayList<>();
        for (Parameter param : odeSystem.getAvailableParameters().values()) {
            if (!param.isSubstituted()) {
                paramValues.add(new ParameterValue(param, point.getValue(param.getIndex())));
            }
        }
        return paramValues;
    }

    protected abstract OctaveDouble rawSimulation(Point point, OctaveOdeSystem odeSystem, long numberOfIterations, PrecisionConfiguration precision);
}
