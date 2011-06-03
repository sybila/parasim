/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim.computation.simulation;

/**
 *
 * @author sven
 */
public enum TrajectoryStatus
{
    /** Simulation is being computed, end not yet reached end. */
    TRAJECTORY_COMPUTING,
    /** Trajectory reached defined maximum time. */
    TRAJECTORY_END_TIMEOUT,
    /** Trajectory reached defined minimum bound in some spatial dimension. */
    TRAJECTORY_END_MINIMUM,
    /** Trajectory reached defined maximum bound in some spatial dimension. */
    TRAJECTORY_END_MAXIMUM,
    /** Trajectory has reached a cycle. */
    TRAJECTORY_END_CYCLE,
    /** Trajectory could not be computed with specified precision. */
    TRAJECTORY_ERROR_PRECISION;

    public static TrajectoryStatus fromInt(int status) {
	switch(status) {
            case 0:
                return TRAJECTORY_COMPUTING;
            case 1:
		return TRAJECTORY_END_TIMEOUT;
            case 2:
		return TRAJECTORY_END_MINIMUM;
            case 3:
		return TRAJECTORY_END_MAXIMUM;
            case 4:
		return TRAJECTORY_END_CYCLE;
            case 5:
		return TRAJECTORY_ERROR_PRECISION;
            default:
		//return null;
		throw new IllegalStateException("There is no status corresponding to the number [" + status + "].");
        }
    }

}
