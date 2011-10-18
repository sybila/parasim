package org.sybila.parasim.computation.density.distancecheck.cpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.Distance;
import org.sybila.parasim.computation.density.DistanceMetric;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.TrajectoryNeighborhood;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ListTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TestOnePairAbsoluteDistanceChecker {

    private static final int DATABLOCK_SIZE = 4;
    private static final int DIMENSION = 3;
    private static final int TRAJECTORY_LENGTH = 10;
    private TrajectoryNeighborhood<Trajectory> simpleNeighborHood;
    private DataBlock<Trajectory> simpleDataBlock;

    @Test
    public void testCheckSimple() {
        DistanceCheckedDataBlock<Trajectory> dataBlock = new OnePairDistanceChecker().check(getConfiguration(), getSimpleDataBlock());
        for (int t = 0; t < DATABLOCK_SIZE; t++) {
            for (int dim = 0; dim < DIMENSION; dim++) {
                assertTrue(dataBlock.getDistances(t).get(dim).isValid());
            }
        }
    }

    private Configuration<Trajectory> getConfiguration() {
        return new Configuration<Trajectory>() {

            private float[] distance;
            private float MAX_DISTANCE = 1;
            
            public float[] getMaxAbsoluteDistance() {
                if (distance == null) {
                    distance = new float[DIMENSION];
                    for(int dim = 0; dim < DIMENSION; dim++) {
                        distance[dim] = MAX_DISTANCE;
                    }
                }
                return distance;
            }

            @Override
            public TrajectoryNeighborhood<Trajectory> getNeighborhood() {
                return getSimpleNeighborHood();
            }

            public DistanceMetric getDistanceMetric() {
                return new DistanceMetric() {
                    
                    public Distance distance(float[] first, float[] second) {
                        final float[] distance = new float[first.length];
                        float maxDistance = 0;
                        for(int dim=0; dim<first.length; dim++) {
                            distance[dim] = Math.abs(first[dim] - second[dim]) / getMaxAbsoluteDistance()[dim];
                            if (distance[dim] > maxDistance) {
                                maxDistance = distance[dim];
                            }
                        }
                        final float maxDistanceFinal = maxDistance;
                        return new Distance() {

                            public boolean isValid() {
                                return value() < MAX_DISTANCE;
                            }

                            public boolean isValid(int dimensionIndex) {
                                return value(dimensionIndex) < MAX_DISTANCE;
                            }

                            public float value() {
                                return maxDistanceFinal;
                            }

                            public float value(int dimensionIndex) {
                                return distance[dimensionIndex];
                            }
                        };
                    }

                    public Distance distance(Point first, Point second) {
                        return distance(first.toArray(), second.toArray());
                    }
                };
            }
        };
    }
    
    private DataBlock<Trajectory> getSimpleDataBlock() {
        if (simpleDataBlock == null) {
            Trajectory[] trajectories = new Trajectory[DATABLOCK_SIZE];
            for (int t = 0; t < DATABLOCK_SIZE; t++) {
                List<Point> points = new ArrayList<Point>(TRAJECTORY_LENGTH);
                for (int p = 0; p < TRAJECTORY_LENGTH; p++) {
                    float[] values = new float[DIMENSION];
                    for (int dim = 0; dim < DIMENSION; dim++) {
                        values[dim] = (float) (0.1 * t + 0.01 * p + 0.001 * dim);
                    }
                    points.add(new ArrayPoint(values, (float) p));
                }
                trajectories[t] = new ListTrajectory(points);
            }
            simpleDataBlock = new ArrayDataBlock<Trajectory>(trajectories);
        }
        return simpleDataBlock;
    }
    
    private TrajectoryNeighborhood<Trajectory> getSimpleNeighborHood() {
        if (simpleNeighborHood == null) {
            final Map<Trajectory, DataBlock<Trajectory>> neighborhoods = new HashMap<Trajectory, DataBlock<Trajectory>>();
            for (int t = 0; t < DATABLOCK_SIZE; t++) {
                Trajectory[] trajectories = new Trajectory[DATABLOCK_SIZE - 1];
                int index = 0;
                for (int i = 0; i < DATABLOCK_SIZE; i++) {
                    if (i != t) {
                        trajectories[index] = getSimpleDataBlock().getTrajectory(i);
                        index++;
                    }
                }
                neighborhoods.put(getSimpleDataBlock().getTrajectory(t), new ArrayDataBlock<Trajectory>(trajectories));
            }
            simpleNeighborHood = new TrajectoryNeighborhood<Trajectory>() {

                @Override
                public DataBlock<Trajectory> getNeighbors(Trajectory trajectory) {
                    return neighborhoods.get(trajectory);
                }
                
            };
        }
        return simpleNeighborHood;
    }    
}
