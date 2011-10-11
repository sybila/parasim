
package org.sybila.parasim.computation.distancechecking;

import java.util.List;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface DataBlock<T extends Trajectory> extends org.sybila.parasim.computation.DataBlock<T> {
    
    List<Float> getDistances(int index);
    
}
