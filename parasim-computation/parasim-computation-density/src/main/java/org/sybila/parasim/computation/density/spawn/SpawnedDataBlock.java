package org.sybila.parasim.computation.density.spawn;

import org.sybila.parasim.computation.TrajectoryNeighborhood;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

public interface SpawnedDataBlock<T extends Trajectory> extends DataBlock<T> {
    
    TrajectoryNeighborhood<T> getNeighborhood();
    
}
