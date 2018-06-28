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
package org.sybila.parasim.computation.simulation.lsoda;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math.ode.DerivativeException;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OctaveOdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.*;

/**
 * @author <a href="mailto:pejznoch@gmail.com">Ales Pejznoch</a>
 */
public class LsodaSimulationEngine implements SimulationEngine {

    LsodaWrapper lsodaWrapper = new LsodaWrapper();

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
    }

    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem,
                               double timeLimit, PrecisionConfiguration precision)
            throws Exception {

        // load parameter values
        List<ParameterValue> paramValues = loadParameterValues(point, odeSystem);
        // create substituted octave ode system
        OctaveOdeSystem octaveOdeSystem = paramValues.isEmpty() ? new OctaveOdeSystem(odeSystem) : new OctaveOdeSystem(odeSystem.substitute(paramValues));
        // compute
        long numOfIterations = Math.round(Math.ceil((1.05 * timeLimit - point.getTime()) / precision.getTimeStep()));
        float[] loadedData = lsodaWrapper.rawSimulation(point, octaveOdeSystem, numOfIterations, precision);
/*/
        try {
            BufferedWriter outputWriter = null;
            outputWriter = new BufferedWriter(new FileWriter("lsoda.log"));
            outputWriter.write(Arrays.toString(loadedData));
        } catch(IOException ie) {
            ie.printStackTrace();
        }
//*/
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
            return new ArrayTrajectory(loadedData, l_times, point.getDimension());
        } else {
            return new ArrayTrajectory(point, loadedData, l_times, octaveOdeSystem.dimension());
        }
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
}
