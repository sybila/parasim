package org.sybila.parasim.model.distance;

/**
 * Represents distance between two objects.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Distance {

    /**
     * FIXME
     *
     * @return
     */
    boolean isValid();

    /**
     * FIXME
     *
     * @param dimensionIndex
     * @return
     */
    boolean isValid(int dimensionIndex);

    /**
     * Returns the overall distance of two objects as a real number.
     * @return Distance as a real number.
     */
    float value();

    /**
     * Returns the distance of two objects in the given dimension.
     * @param dimensionIndex Index of the dimension in which to return distance.
     * @return Distance on dimension with index dimensionIndex.
     */
    float value(int dimensionIndex);
    
}
