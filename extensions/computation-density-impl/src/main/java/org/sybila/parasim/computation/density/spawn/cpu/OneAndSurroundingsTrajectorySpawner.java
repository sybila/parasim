package org.sybila.parasim.computation.density.spawn.cpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.EmptyDataBlock;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OneAndSurroundingsTrajectorySpawner extends AbstractTrajectorySpawner {

    private Map<Point, Trajectory> alreadySpawnedCollisionTrajectories = new HashMap<Point, Trajectory>();
    
    @Override
    protected SpawnedResult spawnTrajectories(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        // find dimension which the first points of the given trajectories differ in
        int diffDimension = -1;
        for (int dim=0; dim<trajectory.getDimension(); dim++) {
            if (trajectory.getFirstPoint().getValue(dim) != neighbor.getFirstPoint().getValue(dim)) {
                if (diffDimension != -1) {
                    throw new IllegalStateException("The first points of the given trajectories differ in more than one dimension.");
                }
                diffDimension = dim;
            }
        }
        // compute half of their distance
        float radius = Math.abs(trajectory.getFirstPoint().getValue(diffDimension) - neighbor.getFirstPoint().getValue(diffDimension)) / 2;
        // create middle seed, surely the middle seed hasn't been created before
        float[] middleSeedData = trajectory.getFirstPoint().toArrayCopy();
        middleSeedData[diffDimension] = (trajectory.getFirstPoint().getValue(diffDimension) - neighbor.getFirstPoint().getValue(diffDimension)) / 2;
        Trajectory middleTrajectory = new PointTrajectory(trajectory.getFirstPoint().getTime(), middleSeedData);
        // memory for spawned trajectories
        List<Trajectory> neighborTrajectories = new ArrayList<Trajectory>();
        List<Trajectory> spawnedTrajectories = new ArrayList<Trajectory>();
        List<Trajectory> spawnedSecondaryTrajectories = new ArrayList<Trajectory>();
        // create neighbor trajectories which can have collision
        for (int dim=0; dim<trajectory.getDimension(); dim++) {
            if (dim == diffDimension) {
                continue;
            }
            for (int sign=-1; sign<=1; sign+=2) {
                float[] newPointData = middleTrajectory.getFirstPoint().toArrayCopy();
                newPointData[dim] += sign*radius;
                Trajectory newTrajectory = new PointTrajectory(trajectory.getFirstPoint().getTime(), newPointData);
                if (alreadySpawnedCollisionTrajectories.containsKey(newTrajectory.getFirstPoint())) {
                    newTrajectory = alreadySpawnedCollisionTrajectories.get(newTrajectory.getFirstPoint());
                } else {
                    alreadySpawnedCollisionTrajectories.put(newTrajectory.getFirstPoint(), newTrajectory);
                    spawnedSecondaryTrajectories.add(newTrajectory);
                }
                neighborTrajectories.add(newTrajectory);
            }
        }
        // reorganize
        Map<Trajectory, DataBlock<Trajectory>> neighborhood = new HashMap<Trajectory, DataBlock<Trajectory>>(neighborTrajectories.size() + 1);
        neighborhood.put(middleTrajectory, new ListDataBlock<Trajectory>(neighborTrajectories));
        for(Trajectory t : spawnedTrajectories) {
            neighborhood.put(t, EmptyDataBlock.EMPTY_DATA_BLOCK);
        }
        return new SpawnedResult(neighborhood, spawnedSecondaryTrajectories);
    }
}
