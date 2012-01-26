package org.sybila.parasim.computation.density.spawn.cpu;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.distance.Distance;
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
            spawnTrajectory(trajectory, neighbor, distance),
            new ArrayDataBlock<Trajectory>(
                new Trajectory[] {
                    trajectory,
                    neighbor
                }
            )
        );
        return new SpawnedResult(neighborhood);
    }
    
    private Trajectory spawnTrajectory(Trajectory trajectory, Trajectory neighbor, Distance distance) {
        float[] data = new float[trajectory.getDimension()];
        Point firstPoint = trajectory.getFirstPoint();
        Point secondPoint = neighbor.getFirstPoint();
        for(int dim=0; dim < trajectory.getDimension(); dim++) {
            data[dim] = (firstPoint.getValue(dim) + secondPoint.getValue(dim))/2;
        }
        return new PointTrajectory(new ArrayPoint(data, firstPoint.getTime()));
    }
    
}