package org.sybila.parasim.computation.density.api;


import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.TrajectoryNeighborhood;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Configuration {
   
    PointDistanceMetric getDistanceMetric();
    
    TrajectoryNeighborhood<Trajectory> getNeighborhood();

    int getStartIndex(int index, int neighborIndex);
    
}
