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


    /**
     * caching simulation time (can save time if the last simulation lasts the same time as the previous)
     */
    private float[] l_times;
    /**
     * start time of last simulation
     */
    private float l_startTime;
    /**
     * end of last simulation
     */
    private double l_endTime;
    /**
     * time step of last simulation
     */
    private float timeStep;

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
        long numOfIterations = Math.round(Math.ceil((1.05 * timeLimit - point.getTime()) / precision.getTimeStep()));
        if (numOfIterations > getStepLimit()) {
            throw new IllegalStateException("Can't simulate the trajectory because the number of iterations <" + numOfIterations + "> is higher than the given limit <" + getStepLimit() + ">.");
        }
        double[] loadedData = rawSimulation(point, octaveOdeSystem, numOfIterations, precision).getData();
        float[] data = new float[loadedData.length];
        for (int dim = 0; dim < octaveOdeSystem.dimension(); dim++) {
            for (int i = 0; i < loadedData.length / octaveOdeSystem.dimension(); i++) {
                data[dim + i * octaveOdeSystem.dimension()] = (float) loadedData[dim * (loadedData.length / octaveOdeSystem.dimension()) + i];
            }
        }
        //if simulation time changed, rewrite cached time array
        if(l_startTime != point.getTime() || l_endTime != timeLimit || timeStep != precision.getTimeStep()){
            l_startTime = point.getTime();
            l_endTime = timeLimit;
            timeStep = precision.getTimeStep();
            l_times = new float[loadedData.length / octaveOdeSystem.dimension()];
            float time = point.getTime();
            for (int i = 0; i < l_times.length; i++) {
                time += precision.getTimeStep();
                l_times[i] = time;
            }
        }
        if (paramValues.isEmpty()) {
            return new ArrayTrajectory(data, l_times, point.getDimension());
        } else {
            return new ArrayTrajectory(point, data, l_times, octaveOdeSystem.dimension());
        }
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
