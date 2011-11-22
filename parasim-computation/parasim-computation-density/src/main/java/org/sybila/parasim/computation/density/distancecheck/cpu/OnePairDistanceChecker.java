package org.sybila.parasim.computation.density.distancecheck.cpu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.model.distance.Distance;
import org.sybila.parasim.computation.density.distancecheck.DistanceChecker;
import org.sybila.parasim.computation.density.distancecheck.ListDistanceCheckedDataBlock;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OnePairDistanceChecker implements DistanceChecker<Configuration<Trajectory>, DistanceCheckedDataBlock> {

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
    public DistanceCheckedDataBlock check(Configuration<Trajectory> congfiguration, DataBlock<Trajectory> trajectories) {
        if (congfiguration == null) {
            throw new IllegalArgumentException("The parameter configuration is null.");
        }
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        List<List<Distance>> distances = new ArrayList<List<Distance>>(trajectories.size());
        for(int index=0; index < trajectories.size(); index++) {
            List<Distance> trajectoryDistances = new ArrayList<Distance>(congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index)).size());
            DataBlock<Trajectory> neighbors = congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index));
            for (Trajectory trajectory: neighbors) {
                trajectoryDistances.add(checkTrajectoriesDistance(congfiguration, trajectories.getTrajectory(index), trajectory));
            }
            distances.add(Collections.unmodifiableList(trajectoryDistances));
        }
        return new ListDistanceCheckedDataBlock(trajectories, distances);
    }
    
    /**
     * Checks distance between two trajectories
     * 
     * @param configuration
     * @param first the first trajectory
     * @param second the second trajectory
     * @return the biggest ratio between measured and required distance
     */
    private Distance checkTrajectoriesDistance(Configuration configuration, Trajectory first, Trajectory second) {
        Distance distance = null;
        Iterator<Point> firstIterator = first.iterator();
        Iterator<Point> secondIterator = second.iterator();
        
        while(firstIterator.hasNext() && secondIterator.hasNext()) {
            Distance currentDistance = getNextDistance(configuration, firstIterator, secondIterator);
            if (currentDistance != null) {
                if (distance != null) {
                    if (distance.value() > currentDistance.value()) {
                        distance = currentDistance;
                    }
                }
                else {
                    distance = currentDistance;
                }
            }
        }
        return distance;
    }
    
    private Distance getNextDistance(Configuration configuration, Iterator<Point> firstIterator, Iterator<Point> secondIterator) {
        Point firstPoint = firstIterator.next();
        Point secondPoint = secondIterator.next();
        if (firstPoint.getTime() == secondPoint.getTime()) {
            return configuration.getDistanceMetric().distance(firstPoint, secondPoint);
        }
        if (firstPoint.getTime() > secondPoint.getTime()) {
            while(secondIterator.hasNext()) {
                Point currentPoint = secondIterator.next();
                if (currentPoint.getTime() >= firstPoint.getTime()) {
                    return checkPointDistance(configuration, secondPoint, currentPoint, firstPoint);
                }
            }
        }
        else {
            while(firstIterator.hasNext()) {
                Point currentPoint = firstIterator.next();
                if (currentPoint.getTime() >= secondPoint.getTime()) {
                    return checkPointDistance(configuration, firstPoint, currentPoint, secondPoint);
                }
            }
        }
        return null;
    }

    private Distance checkPointDistance(Configuration configuration, Point beginFirstPoint, Point endFirstPoint, Point secondPoint) {
        if (endFirstPoint.getTime() == secondPoint.getTime()) {
            return configuration.getDistanceMetric().distance(endFirstPoint, secondPoint);
        }
        float[] firstData = new float[beginFirstPoint.getDimension()];
        for(int dim=0; dim<firstData.length; dim++) {
            firstData[dim] = (beginFirstPoint.getValue(dim) + endFirstPoint.getValue(dim)) / 2;
        }
        return configuration.getDistanceMetric().distance(firstData, secondPoint.toArray());
    }
    
}

