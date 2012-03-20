package org.sybila.parasim.computation.density.spawn.cpu;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OneTrajectorySpawner extends AbstractTrajectorySpawner {
    
    protected SpawnedResult spawnTrajectories(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        Map<Trajectory, DataBlock<Trajectory>> neighborhood = new HashMap<Trajectory, DataBlock<Trajectory>>();
        neighborhood.put(
            spawnMiddleTrajectory(trajectory, neighbor, distance),
            new ArrayDataBlock<Trajectory>(
                new Trajectory[] {
                    trajectory,
                    neighbor
                }
            )
        );
        return new SpawnedResult(neighborhood, null);
    }
    
    /**
     * Spawns a new trajectory in the middle of distance between trajectory
     * and its neighbor.
     * 
     * @param trajectory
     * @param neighbor
     * @param distance
     * @return a new trajectory
     */
    protected Trajectory spawnMiddleTrajectory(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        return new PointTrajectory(spawnMiddleInitPoint(trajectory, neighbor, distance));
    }
    
    /**
     * Creates a new point in the middle of distance between initial points of trajectory
     * and its neighbor
     * 
     * @param trajectory
     * @param neighbor
     * @param distance
     * @return 
     */
    protected Point spawnMiddleInitPoint(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        float[] data = new float[trajectory.getDimension()];
        Point firstPoint = trajectory.getFirstPoint();
        Point secondPoint = neighbor.getFirstPoint();
        for(int dim=0; dim < trajectory.getDimension(); dim++) {
            data[dim] = (firstPoint.getValue(dim) + secondPoint.getValue(dim))/2;
        }        
        return new ArrayPoint(firstPoint.getTime(), data);
    }
    
}