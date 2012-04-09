package org.sybila.parasim.model.trajectory;

/**
 * Trajectories implementing this interface can be prolonged be using append()
 * and truncated from the end to a specified length using truncate().
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface MutableTrajectory extends Trajectory {

    /**
     * Appends the given trajectory to the end of this trajectory thus increasing
     * it's length.
     *
     * @param trajectory Trajectory to be appended to this one.
     */
    void append(Trajectory trajectory);

    /**
     * Truncates this trajectory to lenght newLength. If newLength is not valid
     * an exception is trown.
     *
     * @param newLength Number of points of this trajectory to preserve
     *        from begining.
     */
    void truncate(int newLength);
}
