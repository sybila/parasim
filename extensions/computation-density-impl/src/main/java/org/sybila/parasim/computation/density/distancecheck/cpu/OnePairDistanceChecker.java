package org.sybila.parasim.computation.density.distancecheck.cpu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.computation.density.api.LimitedDistance;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.distancecheck.api.ListDistanceCheckedDataBlock;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OnePairDistanceChecker implements DistanceChecker {

    /**
     * Checks distance of corresponding points of trajectory and trajectories in its neighborhood.
     * If the required distance is violated, distance checking of the current trajectory 
     * is stopped and the violating distance is memorized.
     * 
     * @param congfiguration
     * @param trajectories
     * @return the biggest ratio between measured and required distance
     */
    @Override
    public DistanceCheckedDataBlock check(Configuration congfiguration, DataBlock<Trajectory> trajectories) {
        if (congfiguration == null) {
            throw new IllegalArgumentException("The parameter configuration is null.");
        }
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        List<List<LimitedDistance>> distances = new ArrayList<List<LimitedDistance>>(trajectories.size());
        List<List<Integer>> trajectoryPosition = new ArrayList<List<Integer>>(trajectories.size());
        List<List<Integer>> neighborPosition = new ArrayList<List<Integer>>(trajectories.size());
        for(int index=0; index < trajectories.size(); index++) {
            List<LimitedDistance> currentDistances = new ArrayList<LimitedDistance>(congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index)).size());
            List<Integer> currentTajectoryPositions = new ArrayList<Integer>(congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index)).size());
            List<Integer> currentNeighborPositions = new ArrayList<Integer>(congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index)).size());
            DataBlock<Trajectory> neighbors = congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index));
            for (Trajectory trajectory: neighbors) {
                DistanceAndPosition distanceAndPosition = checkTrajectoriesDistance(congfiguration, trajectories.getTrajectory(index), trajectory);
                currentDistances.add(distanceAndPosition.distance);
                currentTajectoryPositions.add(distanceAndPosition.trajectoryPosition);
                currentNeighborPositions.add(distanceAndPosition.neighborPosition);
            }
            distances.add(currentDistances);
            trajectoryPosition.add(currentTajectoryPositions);
            neighborPosition.add(currentNeighborPositions);
        }
        return new ListDistanceCheckedDataBlock(trajectories, distances, trajectoryPosition, neighborPosition);
    }
    
    /**
     * Checks distance between two trajectories
     * 
     * @param configuration
     * @param first the first trajectory
     * @param second the second trajectory
     * @return the biggest ratio between measured and required distance
     */
    private DistanceAndPosition checkTrajectoriesDistance(Configuration configuration, Trajectory first, Trajectory second) {
        DistanceAndPosition distance = null;
        Iterator<Point> firstIterator = first.iterator();
        Iterator<Point> secondIterator = second.iterator();
        
        while(firstIterator.hasNext() && secondIterator.hasNext()) {
            DistanceAndPosition currentDistance = getNextDistance(configuration, firstIterator, secondIterator);
            if (currentDistance != null) {
                if (distance != null) {
                    if (distance.distance.value() > currentDistance.distance.value()) {
                        distance.distance = currentDistance.distance;
                    }
                    distance.trajectoryPosition += currentDistance.trajectoryPosition + 1;
                    distance.neighborPosition += currentDistance.neighborPosition + 1;
                }
                else {
                    distance = currentDistance;
                }
            }
        }
        return distance;
    }
    
    private DistanceAndPosition getNextDistance(Configuration configuration, Iterator<Point> firstIterator, Iterator<Point> secondIterator) {
        Point firstPoint = firstIterator.next();
        Point secondPoint = secondIterator.next();
        if (firstPoint.getTime() == secondPoint.getTime()) {
            return new DistanceAndPosition(configuration.getDistanceMetric().distance(firstPoint, secondPoint), 0, 0);
        }
        int trajectoryPosition = 0;
        int neighborPosition = 0;
        if (firstPoint.getTime() > secondPoint.getTime()) {
            while(secondIterator.hasNext()) {
                neighborPosition++;
                Point currentPoint = secondIterator.next();
                if (currentPoint.getTime() >= firstPoint.getTime()) {
                    return new DistanceAndPosition(checkPointDistance(configuration, secondPoint, currentPoint, firstPoint), trajectoryPosition, neighborPosition);
                }
            }
        }
        else {
            while(firstIterator.hasNext()) {
                trajectoryPosition++;
                Point currentPoint = firstIterator.next();
                if (currentPoint.getTime() >= secondPoint.getTime()) {
                    return new DistanceAndPosition(checkPointDistance(configuration, firstPoint, currentPoint, secondPoint), trajectoryPosition, neighborPosition);
                }
                
            }
        }
        return null;
    }

    private LimitedDistance checkPointDistance(Configuration configuration, Point beginFirstPoint, Point endFirstPoint, Point secondPoint) {
        if (endFirstPoint.getTime() == secondPoint.getTime()) {
            return configuration.getDistanceMetric().distance(endFirstPoint, secondPoint);
        }
        float[] firstData = new float[beginFirstPoint.getDimension()];
        for(int dim=0; dim<firstData.length; dim++) {
            firstData[dim] = (beginFirstPoint.getValue(dim) + endFirstPoint.getValue(dim)) / 2;
        }
        return configuration.getDistanceMetric().distance(firstData, secondPoint.toArray());
    }
 
    private class DistanceAndPosition {
        public LimitedDistance distance;
        public int trajectoryPosition;
        public int neighborPosition;
        public DistanceAndPosition(LimitedDistance distance, int trajectoryPosition, int neighborPosition) {
            this.distance = distance;
            this.trajectoryPosition = trajectoryPosition;
            this.neighborPosition = neighborPosition;
        }
    }
    
}
