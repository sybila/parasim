package org.sybila.parasim.computation.density;

import org.sybila.parasim.model.distance.Distance;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.computation.MapTrajectoryNeighborhood;
import org.sybila.parasim.computation.TrajectoryNeighborhood;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.ListTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

public abstract class AbstractDensityTest {

    protected Configuration<Trajectory> createConfiguration(final float expectedDistance, final int dimension, final TrajectoryNeighborhood<Trajectory> neighborhood) {
        return new Configuration<Trajectory>() {

            private float[] distance;

            private float[] getMaxAbsoluteDistance() {
                if (distance == null) {
                    distance = new float[dimension];
                    for (int dim = 0; dim < dimension; dim++) {
                        distance[dim] = expectedDistance;
                    }
                }
                return distance;
            }

            @Override
            public TrajectoryNeighborhood<Trajectory> getNeighborhood() {
                return neighborhood;
            }

            public PointDistanceMetric getDistanceMetric() {
                return new PointDistanceMetric() {

                    public Distance distance(float[] first, float[] second) {
                        final float[] distance = new float[first.length];
                        float maxDistance = 0;
                        for (int dim = 0; dim < first.length; dim++) {
                            distance[dim] = Math.abs(first[dim] - second[dim]) / getMaxAbsoluteDistance()[dim];
                            if (distance[dim] > maxDistance) {
                                maxDistance = distance[dim];
                            }
                        }
                        final float maxDistanceFinal = maxDistance;
                        return new Distance() {

                            public boolean isValid() {
                                return value() < expectedDistance;
                            }

                            public boolean isValid(int dimensionIndex) {
                                return value(dimensionIndex) < expectedDistance;
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
    
    protected DataBlock<Trajectory> createDataBlock(int size, int length, int dimension, float x, float y, float z) {
        Trajectory[] trajectories = new Trajectory[size];
        for (int t = 0; t < size; t++) {
            List<Point> points = new ArrayList<Point>(length);
            for (int p = 0; p < length; p++) {
                float[] values = new float[dimension];
                for (int dim = 0; dim < dimension; dim++) {
                    values[dim] = (float) (x * t + y * p + z * dim);
                }
                points.add(new ArrayPoint(values, (float) p));
            }
            trajectories[t] = new ListTrajectory(points);
        }
        return new ArrayDataBlock<Trajectory>(trajectories);
    }

    protected TrajectoryNeighborhood<Trajectory> createNeighborhood(DataBlock<Trajectory> dataBlock) {
        final Map<Trajectory, DataBlock<Trajectory>> neighborhood = new HashMap<Trajectory, DataBlock<Trajectory>>();
        for (int t = 0; t < dataBlock.size(); t++) {
            List<Trajectory> trajectories = new ArrayList<Trajectory>();
            int index = 0;
            for (int i = t+1; i < dataBlock.size(); i++) {
                trajectories.add(dataBlock.getTrajectory(i));
            }
            neighborhood.put(dataBlock.getTrajectory(t), new ListDataBlock<Trajectory>(trajectories));
        }
        return new MapTrajectoryNeighborhood<Trajectory>(neighborhood);
    }
}
