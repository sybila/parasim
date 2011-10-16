package org.sybila.parasim.computation;

import java.util.HashMap;
import java.util.Map;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Really simple implementation of {@link TrajectoryNeighborhood} using {@link Map}
 * as a storage.
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapTrajectoryNeighborhood<T extends Trajectory> implements TrajectoryNeighborhood<T> {

    Map<Trajectory, DataBlock<T>> neighborhoods = new HashMap<Trajectory, DataBlock<T>>();
    
    @Override
    public DataBlock<T> getNeighbors(Trajectory trajectory) {
        return neighborhoods.get(trajectory);
    }

    @Override
    public void setNeighbors(Trajectory trajectory, DataBlock<T> neighborhood) {
        if (trajectory == null) {
            throw new IllegalArgumentException("The parameter trajectory is null.");
        }
        if (neighborhood == null) {
            throw new IllegalArgumentException("The parameter neighborhood is null.");
        }        
        neighborhoods.put(trajectory, neighborhood);
    }
    
    
    
}
