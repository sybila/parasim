/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the
 *
 * @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.computation.simulation.cpu;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.api.ArraySimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.Status;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.LinkedTrajectory;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryWithNeighborhood;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleAdaptiveStepSimulator implements AdaptiveStepSimulator {

    private final SimulationEngineFactory simulationEngineFactory;

    public SimpleAdaptiveStepSimulator(SimulationEngineFactory simulationEngineFactory) {
        Validate.notNull(simulationEngineFactory);
        this.simulationEngineFactory = simulationEngineFactory;
    }

    @Override
    public <T extends Trajectory> SimulatedDataBlock<T> simulate(AdaptiveStepConfiguration configuration, DataBlock<T> data) {
        SimulationEngine simulationEngine = simulationEngineFactory.simulationEngine(configuration.getMaxNumberOfIterations(), configuration.getPrecisionConfiguration().getMaxRelativeError());
        try {
            List<T> trajectories = new ArrayList<>(data.size());
            Status[] statuses = new Status[data.size()];
            for (int i = 0; i < data.size(); i++) {
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
            return new ArraySimulatedDataBlock<>(new ListDataBlock<>(trajectories), statuses);
        } finally {
            simulationEngine.close();
        }
    }
}
