package org.sybila.parasim.computation.distancechecking;

import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Configuration<T extends Trajectory> {
   
    float[] getMaxAbsoluteDistance();
    
    float[] getMaxRelativeDistance();
    
    TrajectoryNeighborhood<T> getNeighborhood();
   
}
