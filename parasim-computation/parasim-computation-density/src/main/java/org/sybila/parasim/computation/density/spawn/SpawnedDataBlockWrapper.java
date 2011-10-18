package org.sybila.parasim.computation.density.spawn;

import java.util.Iterator;
import org.sybila.parasim.computation.TrajectoryNeighborhood;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

public class SpawnedDataBlockWrapper<T extends Trajectory> implements SpawnedDataBlock<T> {

    private TrajectoryNeighborhood<T> neighborhood;
    private DataBlock<T> trajectories;
    
    public SpawnedDataBlockWrapper(DataBlock<T> trajectories, TrajectoryNeighborhood<T> neighborhood) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (neighborhood == null) {
            throw new IllegalArgumentException("The parameter neighborhood is null.");
        }
        this.trajectories = trajectories;
        this.neighborhood = neighborhood;
    }
    
    public TrajectoryNeighborhood<T> getNeighborhood() {
        return neighborhood;
    }

    public T getTrajectory(int index) {
        return trajectories.getTrajectory(index);
    }

    public int size() {
        return trajectories.size();
    }

    public Iterator<T> iterator() {
        return trajectories.iterator();
    }
    
}
