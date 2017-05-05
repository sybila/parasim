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
package org.sybila.parasim.computation.simulation.cpu;

import org.apache.commons.lang3.Validate;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.simulation.SimulatorRegistrar;
import org.sybila.parasim.computation.simulation.api.*;
import org.sybila.parasim.computation.simulation.simulationcore.SimCoreSimulationEngine;
import org.sybila.parasim.model.trajectory.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 * @author <a href="mailto:433392@fi.muni.cz">Vojtech Bruza</a>
 */
public class SimpleAdaptiveStepSimulator implements AdaptiveStepSimulator {

    private final SimulationEngineFactory simulationEngineFactory;

    public SimpleAdaptiveStepSimulator(SimulationEngineFactory simulationEngineFactory) {
        Validate.notNull(simulationEngineFactory);
        this.simulationEngineFactory = simulationEngineFactory;
    }

    private static boolean octaveAvailable = false;

    static {
        try {
            //Checking if octave is available on this machine
            Process p = Runtime.getRuntime().exec(new String[]{"octave", "--version"});
            p.waitFor();
            if (p.exitValue() == 0) {
                octaveAvailable = true;
                LoggerFactory.getLogger(SimulatorRegistrar.class).info("Using Octave simulation engine");
            } else {
                octaveAvailable = false;
                LoggerFactory.getLogger(SimulatorRegistrar.class).info("Octave not working, using Simulation Core simulation engine");
            }
        } catch (IOException | InterruptedException ignored) {
            octaveAvailable = false;
            LoggerFactory.getLogger(SimulatorRegistrar.class).info("Octave not available, using Simulation Core simulation engine");
        }
    }

    @Override
    public <T extends Trajectory> SimulatedDataBlock<T> simulate(AdaptiveStepConfiguration configuration, DataBlock<T> data) {
        SimulationEngine simulationEngine;
        if (octaveAvailable){
            simulationEngine = simulationEngineFactory.simulationEngine(configuration.getMaxNumberOfIterations());
        } else {
            simulationEngine = new SimCoreSimulationEngine();
        }
        List<T> trajectories = new ArrayList<>(data.size());
        Status[] statuses = new Status[data.size()];
        for (int i = 0; i < data.size(); i++) {
            synchronized(data.getTrajectory(i).getReference().getTrajectory().getFirstPoint()) {
                if (data.getTrajectory(i).getReference().getTrajectory().getLastPoint().getTime() >= configuration.getSpace().getMaxBounds().getTime()) {
                    statuses[i] = Status.OK;
                    trajectories.add((T)data.getTrajectory(i).getReference().getTrajectory());
                    continue;
                }
                Trajectory simulated = simulationEngine.simulate(data.getTrajectory(i).getLastPoint(), configuration.getOdeSystem(), configuration.getSpace().getMaxBounds().getTime(), configuration.getPrecisionConfiguration());
                LinkedTrajectory trajectory = data.getTrajectory(i) instanceof LinkedTrajectory ? (LinkedTrajectory) data.getTrajectory(i) : (data.getTrajectory(i) instanceof TrajectoryWithNeighborhood ? LinkedTrajectory.createAndUpdateReferenceWithNeighborhood((TrajectoryWithNeighborhood) data.getTrajectory(i)) : LinkedTrajectory.createAndUpdateReference(data.getTrajectory(i)));
                trajectory.append(simulated);
                trajectories.add((T) trajectory);
                if (simulated.getLastPoint().getTime() < configuration.getSpace().getMaxBounds().getTime()) {
                    statuses[i] = Status.TIMEOUT;
                } else {
                    statuses[i] = Status.OK;
                }
            }
        }
        return new ArraySimulatedDataBlock<>(new ListDataBlock<>(trajectories), statuses);
    }

    @Override
    public <T extends Trajectory> T simulate(AdaptiveStepConfiguration configuration, T trajectory) {
        if (trajectory.getLastPoint().getTime() >= configuration.getSpace().getMaxBounds().getTime()) {
            return trajectory;
        }
        SimulationEngine simulationEngine = simulationEngineFactory.simulationEngine(configuration.getMaxNumberOfIterations());
        Trajectory simulated = simulationEngine.simulate(trajectory.getLastPoint(), configuration.getOdeSystem(), configuration.getSpace().getMaxBounds().getTime(), configuration.getPrecisionConfiguration());
        LinkedTrajectory result = trajectory instanceof LinkedTrajectory ? (LinkedTrajectory) trajectory : (trajectory instanceof TrajectoryWithNeighborhood ? LinkedTrajectory.createAndUpdateReferenceWithNeighborhood((TrajectoryWithNeighborhood) trajectory) : LinkedTrajectory.createAndUpdateReference(trajectory));
        result.append(simulated);
        return (T) result;
    }
}
