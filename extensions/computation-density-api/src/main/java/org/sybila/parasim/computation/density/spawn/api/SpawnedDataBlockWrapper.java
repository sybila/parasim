package org.sybila.parasim.computation.density.spawn.api;

import java.util.Iterator;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryNeighborhood;

public class SpawnedDataBlockWrapper implements SpawnedDataBlock {

    private TrajectoryNeighborhood<Trajectory> neighborhood;
    private DataBlock<Trajectory> secondaryTrajectories;
    private DataBlock<Trajectory> trajectories;

    public SpawnedDataBlockWrapper(DataBlock<Trajectory> trajectories, TrajectoryNeighborhood<Trajectory> neighborhood, DataBlock<Trajectory> secondaryTrajectories) {
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        if (neighborhood == null) {
            throw new IllegalArgumentException("The parameter neighborhood is null.");
        }
        if (secondaryTrajectories == null) {
            throw new IllegalArgumentException("The parameter secondaryTrajectories is null.");
        }
        this.trajectories = trajectories;
        this.neighborhood = neighborhood;
        this.secondaryTrajectories = secondaryTrajectories;
    }

    @Override
    public TrajectoryNeighborhood<Trajectory> getNeighborhood() {
        return neighborhood;
    }

    @Override
    public Trajectory getTrajectory(int index) {
        return trajectories.getTrajectory(index);
    }

    @Override
    public DataBlock<Trajectory> getSecondaryTrajectories() {
        return secondaryTrajectories;
    }

    @Override
    public int size() {
        return trajectories.size();
    }

    @Override
    public Iterator<Trajectory> iterator() {
        return trajectories.iterator();
    }
}
