package org.sybila.parasim.model.trajectory;

/**
 * Represents a trajectory with a possible cycle at the end.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface CyclicTrajectory extends Trajectory
{
    /**
     *
     * @return
     */
    boolean hasCycle();

    
}
