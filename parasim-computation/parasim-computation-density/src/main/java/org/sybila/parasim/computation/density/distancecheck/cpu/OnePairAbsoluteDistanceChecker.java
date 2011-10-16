package org.sybila.parasim.computation.density.distancecheck.cpu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.sybila.parasim.computation.density.Configuration;
import org.sybila.parasim.computation.density.distancecheck.DistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.Distance;
import org.sybila.parasim.computation.density.distancecheck.DistanceChecker;
import org.sybila.parasim.computation.density.distancecheck.ListDistanceCheckedDataBlock;
import org.sybila.parasim.computation.density.distancecheck.SimpleDistance;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OnePairAbsoluteDistanceChecker implements DistanceChecker<Configuration, DistanceCheckedDataBlock> {

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
    public DistanceCheckedDataBlock check(Configuration congfiguration, org.sybila.parasim.computation.DataBlock<Trajectory> trajectories) {
        if (congfiguration == null) {
            throw new IllegalArgumentException("The parameter configuration is null.");
        }
        if (trajectories == null) {
            throw new IllegalArgumentException("The parameter trajectories is null.");
        }
        List<List<Distance>> distances = new ArrayList<List<Distance>>(trajectories.size());
        for(int index=0; index < trajectories.size(); index++) {
            List<Distance> trajectoryDistances = new ArrayList<Distance>(congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index)).size());
            org.sybila.parasim.computation.DataBlock<Trajectory> neighbors = congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index));
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
        float[] distances = new float[first.getDimension()];
        float maxDistance = 0;
        Iterator<Point> firstIterator = first.iterator();
        Iterator<Point> secondIterator = second.iterator();
        while(firstIterator.hasNext() && secondIterator.hasNext()) {
            Point firstPointBegin = firstIterator.next();
            Point secondPointBegin = secondIterator.next();
            float[] pointDistance; 
            if (firstPointBegin.getTime() < secondPointBegin.getTime()) {
                Point firstPointEnd = null;
                while(firstIterator.hasNext()) {
                    Point p = firstIterator.next();
                    if (p.getTime() > secondPointBegin.getTime()) {
                        firstPointEnd = p;
                    }
                }
                if (firstPointEnd == null) {
                    break;
                }
                pointDistance = checkPointDistance(firstPointBegin, firstPointEnd, secondPointBegin);
            }
            else if (firstPointBegin.getTime() > secondPointBegin.getTime()) {
                Point secondPointEnd = null;
                while(secondIterator.hasNext()) {
                    Point p = secondIterator.next();
                    if (firstPointBegin.getTime() < p.getTime()) {
                        secondPointEnd = p;
                    }
                }                
                if (secondPointEnd == null) {
                    break;
                }         
                pointDistance = checkPointDistance(secondPointBegin, secondPointEnd, firstPointBegin);
            }
            else {
                pointDistance = checkPointDistance(firstPointBegin, secondPointBegin);
            }
            for(int dim=0; dim<pointDistance.length; dim++) {
                pointDistance[dim] = pointDistance[dim] / configuration.getMaxAbsoluteDistance()[dim];
                if (pointDistance[dim] > maxDistance) {
                    maxDistance = pointDistance[dim];
                    distances = pointDistance;
                }
            }
            if (maxDistance > 1) {
                break;
            }
        }
        return new SimpleDistance(distances, maxDistance);
    }
    
    private float[] checkPointDistance(Point first, Point second) {
        float distance[] = new float[first.getDimension()];
        for(int dim=0; dim<first.getDimension(); dim++) {
            distance[dim] = Math.abs(first.getValue(dim) - second.getValue(dim));
        }
        return distance;
    }
    
    private float[] checkPointDistance(Point firstBegin, Point firstEnd, Point second) {
        float distance[] = new float[firstBegin.getDimension()];
        float ratio = second.getTime() / (firstBegin.getTime() + firstEnd.getTime());
        for(int dim=0; dim<firstBegin.getDimension(); dim++) {
            distance[dim] = Math.abs((firstBegin.getValue(dim) + firstEnd.getValue(dim)) * ratio - second.getValue(dim));
        }
        return distance;
    }
    
}
