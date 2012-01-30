package org.sybila.parasim.model.space;

import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Space {
    
    /**
     * @return a space dimension
     */
    int getDimension();

    /**
     * @param dimension
     * @return size of space in the given dimension
     */
    float getSize(int dimension);
    
    /**
     * Checks whether the given point is in the space (it doesn't check time).
     * @param point
     * @return 
     */
    boolean isIn(Point point);
 
    /**
     * Checks whether the given point is in the space.
     * @param point
     * @return 
     */
    boolean isIn(float[] point);
    
}
