
package parasim.computation.simulation;

/**
 * Extends the BatchSimulator by enabling the simulation of points
 * with specified density in the form of maximum distance of two consecutive
 * points of a single trajectory on every dimension.
 *
 * @author sven
 */
public interface DenseSimulator extends BatchSimulator, ErrorControlSimulator {

    /**          
     * @param distances maximum distance of two consecutive points
     *        of one trajectory in all dimensions. If some dimension is not
     *        to be checked set it to 0.     
     */
    void setMaxPointDistances(float[] distances);

    float[] getMaxPointDistances();    

}
