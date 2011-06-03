/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.computation.simulation;

/**
 *
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public interface SimulatorFactory {

    /**
     * Creates a new simulator with memory requirements given by parameters.
     *
     * @param maxTrajectoryCount The maximum number of trajectories to be
     *        simulated in one batch.
     * @param maxSegmentLength The maximum number of points to add to each
     *        trajectory in one batch.
     * @param dimension the number The dimension of all points on trajectories.
     * @return The new simulator on success or null on failure.
     */
    public BatchSimulator createSimulator(
        int maxTrajectoryCount,
        int maxSegmentLength,
        int dimension);

    void setSimulatorManager(); //FIXME

}
