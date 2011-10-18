package org.sybila.parasim.computation.density.spawn.cpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.computation.MapTrajectoryNeighborhood;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.Distance;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.spawn.SpawnedDataBlock;
import org.sybila.parasim.computation.density.spawn.SpawnedDataBlockWrapper;
import org.sybila.parasim.computation.density.spawn.TrajectorySpawner;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointTrajectory;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OneTrajectorySpawner implements TrajectorySpawner<Configuration<Trajectory>, SpawnedDataBlock<Trajectory>> {

    @Override
    public SpawnedDataBlock<Trajectory> spawn(Configuration<Trajectory> configuration, DistanceCheckedDataBlock<Trajectory> trajectories) {
        List<Trajectory> newTrajectories = new ArrayList<Trajectory>();
        Map<Trajectory, DataBlock<Trajectory>> neighborhood = new HashMap<Trajectory, DataBlock<Trajectory>>();
        for(int i=0; i<trajectories.size(); i++) {
            Trajectory trajectory = trajectories.getTrajectory(i);
            for(int n=0; n<trajectories.getDistances(i).size(); n++) {
                if (!trajectories.getDistances(i).get(n).isValid()) {
                    Trajectory spawnedTrajectory = spawnTrajectory(trajectory, configuration.getNeighborhood().getNeighbors(trajectory).getTrajectory(n), trajectories.getDistances(i).get(n));
                    neighborhood.put(
                        spawnedTrajectory,
                        new ArrayDataBlock<Trajectory>(
                            new Trajectory[] {
                                trajectory,
                                configuration.getNeighborhood().getNeighbors(trajectory).getTrajectory(n)
                            }
                        )
                    );
                    newTrajectories.add(spawnedTrajectory);
                }
            }
        }
        return new SpawnedDataBlockWrapper<Trajectory>(new ListDataBlock<Trajectory>(newTrajectories), new MapTrajectoryNeighborhood<Trajectory>(neighborhood));
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
