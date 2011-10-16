package org.sybila.parasim.computation.density;

import org.sybila.parasim.computation.TrajectoryNeighborhood;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Configuration<T extends Trajectory> {
   
    float[] getMaxAbsoluteDistance();
    
    TrajectoryNeighborhood<T> getNeighborhood();
   
}
