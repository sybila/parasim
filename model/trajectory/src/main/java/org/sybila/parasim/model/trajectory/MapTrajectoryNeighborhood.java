package org.sybila.parasim.model.trajectory;

import java.util.HashMap;
import java.util.Map;

/**
 * Really simple implementation of {@link TrajectoryNeighborhood} using {@link Map}
 * as a storage.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapTrajectoryNeighborhood<T extends Trajectory> implements TrajectoryNeighborhood<T> {

    Map<Trajectory, DataBlock<T>> neighborhoods;

    public MapTrajectoryNeighborhood() {
        this(new HashMap<Trajectory, DataBlock<T>>());
    }

    public MapTrajectoryNeighborhood(Map<Trajectory, DataBlock<T>> neighborhoods) {
        if (neighborhoods == null) {
            throw new IllegalArgumentException("The parameter neighborhoods is null.");
        }
        this.neighborhoods = neighborhoods;
    }

    @Override
    public DataBlock<T> getNeighbors(Trajectory trajectory) {
        return neighborhoods.get(trajectory);
    }

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
