package org.sybila.parasim.computation.density.spawn.cpu;

import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.computation.DataBlock;
import org.sybila.parasim.computation.ListDataBlock;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.Distance;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.spawn.TrajectorySpawner;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DimensionOneTrajectorySpawner implements TrajectorySpawner<Configuration<Trajectory>, DataBlock<Trajectory>> {

    @Override
    public DataBlock<Trajectory> spawn(Configuration<Trajectory> configuration, DistanceCheckedDataBlock<Trajectory> trajectories) {
        List<Trajectory> newTrajectories = new ArrayList<Trajectory>();
        for(int i=0; i<trajectories.size(); i++) {
            Trajectory trajectory = trajectories.getTrajectory(i);
            for(int n=0; n<trajectories.getDistances(i).size(); n++) {
                if (trajectories.getDistances(i).get(n).getMaxDistance() > 1) {
                    newTrajectories.add(spawnTrajectory(trajectory, configuration.getNeighborhood().getNeighbors(trajectory).getTrajectory(n), trajectories.getDistances(i).get(n)));
                }
            }
        }
        return new ListDataBlock<Trajectory>(newTrajectories);
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
