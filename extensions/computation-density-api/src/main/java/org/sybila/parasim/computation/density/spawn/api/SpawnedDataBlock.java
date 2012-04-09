package org.sybila.parasim.computation.density.spawn.api;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryNeighborhood;

public interface SpawnedDataBlock extends DataBlock<Trajectory> {

    TrajectoryNeighborhood<Trajectory> getNeighborhood();

    DataBlock<Trajectory> getSecondaryTrajectories();
}
