package org.sybila.parasim.computation.distancechecking.cpu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.sybila.parasim.computation.distancechecking.Configuration;
import org.sybila.parasim.computation.distancechecking.DataBlock;
import org.sybila.parasim.computation.distancechecking.DistanceChecker;
import org.sybila.parasim.computation.distancechecking.ListDataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OnePairDistanceChecker implements DistanceChecker<Configuration, DataBlock> {

    @Override
    public DataBlock check(Configuration congfiguration, org.sybila.parasim.computation.DataBlock<Trajectory> trajectories) {
        List<List<Float>> distances = new ArrayList<List<Float>>(trajectories.size());
        for(int index=0; index < trajectories.size(); index++) {
            List<Float> trajectoryDistances = new ArrayList<Float>(congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index)).size());
            org.sybila.parasim.computation.DataBlock<Trajectory> neighbors = congfiguration.getNeighborhood().getNeighbors(trajectories.getTrajectory(index));
            for (Trajectory trajectory: neighbors) {
                trajectoryDistances.add(checkTrajectoriesDistance(trajectories.getTrajectory(index), trajectory));
            }
            distances.add(Collections.unmodifiableList(trajectoryDistances));
        }
        return new ListDataBlock(trajectories, distances);
    }
    
    private float checkTrajectoriesDistance(Trajectory first, Trajectory second) {
        float distance = 0;
        Iterator<Point> firstIterator = first.iterator();
        Iterator<Point> secondIterator = second.iterator();
        while(firstIterator.hasNext() && secondIterator.hasNext()) {
            Point firstPointBegin = firstIterator.next();
            Point secondPointBegin = secondIterator.next();
            float pointDistance;
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
            if (pointDistance > distance) {
                distance = pointDistance;
            }
        }
        return distance;
    }
    
    private float checkPointDistance(Point first, Point second) {
        float distance = 0;
        for(int dim=0; dim<first.getDimension(); dim++) {
            float dimDistance = Math.abs(first.getValue(dim) - second.getValue(dim));
            if (dimDistance > distance) {
                distance = dimDistance;
            }
        }
        return distance;
    }
    
    private float checkPointDistance(Point firstBegin, Point firstEnd, Point second) {
        float distance = 0;
        float ratio = second.getTime() / (firstBegin.getTime() + firstEnd.getTime());
        for(int dim=0; dim<firstBegin.getDimension(); dim++) {
            float dimDistance = Math.abs((firstBegin.getValue(dim) + firstEnd.getValue(dim)) * ratio - second.getValue(dim));
            if (dimDistance > distance) {
                distance = dimDistance;
            }
        }
        return distance;
    }
    
}
