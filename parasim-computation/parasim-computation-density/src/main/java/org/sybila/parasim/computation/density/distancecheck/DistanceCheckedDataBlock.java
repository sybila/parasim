
package org.sybila.parasim.computation.density.distancecheck;

import org.sybila.parasim.model.distance.Distance;
import java.util.List;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface DistanceCheckedDataBlock<T extends Trajectory> extends DataBlock<T> {
    
    List<Distance> getDistances(int index);
    
}
