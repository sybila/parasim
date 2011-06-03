
package parasim.computation;

/**
 * Represents a single simulated point in time and space.
 * Time is the 0 dimension and at least one more spatial dimension must
 * be present, therefore getDimension >= 2.
 * 
 * @author sven
 */
public interface Point {

    /**
     * @return Number of dimensions of given point.
     */
    int getDimension();

    /**
     * @return Value of time dimension 0, is equal to getValue(0)
     */
    float getTime();

    /**
     * @param index The dimension of who's value to return.
     * @return Value of given dimension.
     */
    float getValue(int index);

    /**
     * @return Values of all dimensions as an array, time is on dimension 0.
     */
    float[] toArray();

}
