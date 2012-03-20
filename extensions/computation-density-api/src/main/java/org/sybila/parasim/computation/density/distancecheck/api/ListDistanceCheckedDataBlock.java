package org.sybila.parasim.computation.density.distancecheck.api;

import java.util.Iterator;
import java.util.List;
import org.sybila.parasim.computation.density.api.LimitedDistance;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ListDistanceCheckedDataBlock<T extends Trajectory> implements DistanceCheckedDataBlock<T> {
    
    private DataBlock<T> dataBlock;
    private List<List<LimitedDistance>> distances;
    private List<List<Integer>> neighborCheckedPositions;
    private List<List<Integer>> trajectoryCheckedPositions;
    
    public ListDistanceCheckedDataBlock(DataBlock<T> dataBlock, List<List<LimitedDistance>> distances, List<List<Integer>> trajectoryCheckedPositions, List<List<Integer>> neighborCheckedPositions) {
        if (dataBlock == null) {
            throw new IllegalArgumentException("The parameter dataBlock is null.");
        }
        if (distances == null) {
            throw new IllegalArgumentException("The parameter distances is null.");
        }
        if (distances.size() != dataBlock.size()) {
            throw new IllegalArgumentException("The number of distances doesn't correspond to the number of trajectories.");
        }
        if (trajectoryCheckedPositions == null) {
            throw new IllegalArgumentException("The parameter trajectoryCheckedPositions is null.");
        }
        if (trajectoryCheckedPositions.size() != dataBlock.size()) {
            throw new IllegalArgumentException("The number of trajectory checked positions doesn't correspond to the number of trajectories.");
        }
        if (neighborCheckedPositions == null) {
            throw new IllegalArgumentException("The parameter neighborCheckedPositions is null.");
        }
        if (neighborCheckedPositions.size() != dataBlock.size()) {
            throw new IllegalArgumentException("The number of neighbor checked positions doesn't correspond to the number of trajectories.");
        }
        this.dataBlock = dataBlock;
        this.distances = distances;
        this.trajectoryCheckedPositions = trajectoryCheckedPositions;
        this.neighborCheckedPositions = neighborCheckedPositions;
    }

    @Override
    public LimitedDistance getDistance(int index, int neighborIndex) {
        return distances.get(index).get(neighborIndex);
    }

    @Override
    public T getTrajectory(int index) {
        return dataBlock.getTrajectory(index);
    }

    @Override
    public int size() {
        return dataBlock.size();
    }

    @Override
    public Iterator<T> iterator() {
        return dataBlock.iterator();
    }

    @Override
    public int getTrajectoryCheckedPosition(int index, int neighborIndex) {
        return trajectoryCheckedPositions.get(index).get(neighborIndex);
    }

    @Override
    public int getNeighborCheckedPosition(int index, int neighborIndex) {
        return neighborCheckedPositions.get(index).get(neighborIndex);
    }
    
}
