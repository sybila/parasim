
package parasim.computation.simulation;

import java.util.List;

/**
 * Computes numerical simulations of trajectories in batch mode.
 * Since the comuptation is expected to be carried out in a CUDA GPU there
 * is a limit on the memory and therefore the size of batches that can be
 * computed.
 *
 * At the begining the maximum number of trajectories in a batch (maxBatchSize),
 * trajectory length (maxTrajectoryBlockLength) and point dimensions (dimension)
 * are specified.
 *
 * @author sven
 */
public interface BatchSimulator {

    /**
     * @return The maximum number of trajectories to simulate in one batch.
     */
    int getMaxTrajectoryCount();

    /**
     * @return The maximum number of points to be added to each trajectory
     *         in one call of the simulate() method.
     */
    int getMaxSegmentLength();

    /**
     * @return The number of dimensions of points on all trajectories to be
     *         simulated.
     */
    int getDimension();

    /**
     * @param trajectories The trajectories to be prolonged by the
     *        simulate() method.
     */
    void setTrajectories(List<Trajectory> trajectories);

    /**
     * @return All simulated trajectories with added points.
     */
    List<Trajectory> getTrajectories();

    /**
     * @return Simulation statuses of all the simulated trajectories.
     */
    List<TrajectoryStatus> getTrajectoryStatuses();

    /**
     * @param maxSegmentLength The prefered number of points to be added
     *        in one call to simulate() it must be smaller than maxSegmentLength.
     */
    void setSegmentLength(int segmentLength);

    int getSegmentLength();

    /**
     * @param interations The amount of work to do inside the simulate method
     *        during the simulation of each trajectory.
     */
    void setMaxIterations(int interations);

    int getMaxIterations();

    /**
     * @param minBounds The lower bounds on all space-time dimensions.
     *        The simulation of a trajectory stops if this value is reached.     
     */
    void setMinBounds(float[] minBounds);

    float[] getMinBounds();

    /**
     * @param maxBounds The upper bounds on all space-time dimensions.
     *        The simulation of a trajectory stops if this value is reached.
     *        Time is on dimension 0.
     */
    void setMaxBounds(float[] maxBounds);

    float[] getMaxBounds();

    /**
     * Launches the numerical simulation of a batch of trajectories starting
     * from their last points with given parameters and limitations.
     * WARNING The trajectories are modified during the call to simulate since
     * new points are appended to them.
     */
    public boolean simulate(); //TODO maybe should return some different value

    public void destroy(); //FIXME maybe finalize() instead?
}
