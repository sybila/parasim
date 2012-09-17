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
package org.sybila.parasim.computation.simulation.cpu;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.api.ArraySimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.Status;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.ode.OctaveOdeSystem;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OctaveSimulator implements AdaptiveStepSimulator {

    @Override
    public SimulatedDataBlock simulate(AdaptiveStepConfiguration configuration, DataBlock<Trajectory> data) {
        OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        List<ParameterValue> parameters = new ArrayList<>();
        octave.eval("lsode_options(\"step limit\", " + configuration.getMaxNumberOfIterations() + ");");
        if (configuration.getPrecisionConfiguration().getMaxRelativeError() > 0) {
            octave.eval("lsode_options(\"relative tolerance\", " + configuration.getPrecisionConfiguration().getMaxRelativeError() + ");");
        }
        Trajectory[] trajectories = new Trajectory[data.size()];
        Status[] statuses = new Status[data.size()];
        for (int i = 0; i < data.size(); i++) {
            trajectories[i] = simulateTrajectory(octave, configuration.getOdeSystem(), configuration, data.getTrajectory(i).getLastPoint());
            if (trajectories[i].getLastPoint().getTime() < configuration.getSpace().getMaxBounds().getTime()) {
                statuses[i] = Status.TIMEOUT;
            } else {
                statuses[i] = Status.OK;
            }
        }
        octave.close();
        return new ArraySimulatedDataBlock(new ArrayDataBlock(trajectories), statuses);
    }

    private Trajectory simulateTrajectory(OctaveEngine octave, OdeSystem odeSystem, AdaptiveStepConfiguration configuration, Point initialPoint) {
        List<ParameterValue> paramValues = new ArrayList<>();
        for (Parameter param: odeSystem.getAvailableParameters().values()) {
            if (!param.isSubstituted()) {
                paramValues.add(new ParameterValue(param, initialPoint.getValue(param.getIndex())));
            }
        }
        OctaveOdeSystem octaveOdeSystem = paramValues.isEmpty() ? new OctaveOdeSystem(odeSystem) : new OctaveOdeSystem(odeSystem.substitute(paramValues));
        octave.eval(octaveOdeSystem.octaveString());
        octave.eval("i = " + Arrays.toString(initialPoint.toArray(octaveOdeSystem.dimension())) + ";");
        int numOfIterations = Math.min(Math.round((configuration.getSpace().getMaxBounds().getTime() - initialPoint.getTime()) / configuration.getPrecisionConfiguration().getTimeStep()), configuration.getMaxNumberOfIterations());
        octave.eval("t = linspace(" + initialPoint.getTime() + ", " + numOfIterations * configuration.getPrecisionConfiguration().getTimeStep() + ", " + numOfIterations + ");");
        octave.eval("y = lsode(\"" + octaveOdeSystem.octaveName() + "\", i, t);");
        OctaveDouble y = octave.get(OctaveDouble.class, "y");
        double[] loadedData = y.getData();
        float[] data = new float[loadedData.length];
        for (int dim = 0; dim < octaveOdeSystem.dimension(); dim++) {
            for (int i = 0; i < loadedData.length / octaveOdeSystem.dimension(); i++) {
                data[dim + i * octaveOdeSystem.dimension()] = (float) loadedData[dim * (loadedData.length / octaveOdeSystem.dimension()) + i];
            }
        }
        float[] times = new float[loadedData.length / octaveOdeSystem.dimension()];
        float time = initialPoint.getTime();
        for (int i = 0; i < times.length; i++) {
            time += configuration.getPrecisionConfiguration().getTimeStep();
            times[i] = time;
        }
        if (paramValues.isEmpty()) {
            return new ArrayTrajectory(data, times, initialPoint.getDimension());
        } else {
            return new ArrayTrajectory(initialPoint, data, times, octaveOdeSystem.dimension());
        }
    }

    public static boolean isAvailable() {
        try {
            new OctaveEngineFactory().getScriptEngine();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
