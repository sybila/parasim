package org.sybila.parasim.model.trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ArrayPointLocator {
    
    /**
     * Returns position of point in array (position of the first dimension value)
     * followed by other dimension values.
     *
     * 
     * @param pointIndex point index
     * @return point index 
     */
    int getPointPosition(int pointIndex);
    
    /**
     * Returns position of time value in array.
     * 
     * @param pointIndex index
     * @return time index
     */
    int getTimePosition(int pointIndex);
    
}
