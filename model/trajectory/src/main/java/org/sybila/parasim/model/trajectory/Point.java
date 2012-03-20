package org.sybila.parasim.model.trajectory;

/**
 * Represents a single simulated point in time and space.
 * 
 * @author <a href="mailto:xdrazan@fi.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Point extends Iterable<Float> {

    /**
     * @return Number of dimensions of given point.
     */
    int getDimension();

    /**
     * @return time of the point
     */
    float getTime();

    /**
     * @param index The dimension of who's value to return.
     * @return Value of given dimension.
     */
    float getValue(int index);

    /**
     * This method returns always the same instance of array (in opposite to {@link  Point#toArrayCopy()).
     * 
     * @return Values of all dimensions as an array without time
     */
    float[] toArray();
    
    /**
     * This method always returns a new instance of array (in opposite to {@link Point#toArray()}).
     * 
     * @return Values of all dimension as an array without time
     */
    float[] toArrayCopy();
}
