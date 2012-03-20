package org.sybila.parasim.computation.cycledetection.api;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Enables cycle detecion on a trajectory while specifying how many points
 * at most to inspect on the trajectory.
 *
 * A cycle is understood as two points on the trajectory being marked as
 * similar.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public interface CycleDetector<T extends Trajectory> {

    /**
     * Performs cycle detection on the trajectory while carrying out at most
     * stepLimit point comparisons. The actuall number of points compared
     * is returned.
     *
     * If stepLimit = 0 then the computation continues as long as it takes
     * to detect a cycle or reach the end of the trajectory.
     *
     * @param trajectory Trajectory on which to carry out cycle detection.
     * @param stepLimit Maximum number of point comparisons to carry out.
     * @return Actual number of comparisons carried out.
     */
    int detectCycle(T trajectory, int stepLimit);

    /**
     * Before a cycle has been detected false is returned, if a cycle
     * is detected true will be returned from that time on.
     *
     * @return True if cycle has been already detected, false otherwise.
     */
    boolean cycleDetected();

    /**
     * If a cycle has been detected on a trajectory then two points have been
     * found similar. In this case the first one is returned.
     * If no cycle is detected null is returned.
     *
     * @return First of the two similar points or null.
     */
    Point getCycleStart();

    int getCycleStartPosition();

    /**
     * If a cycle has been detected on a trajectory then two points have been
     * found similar. In this case the second one is returned.
     * If no cycle is detected null is returned.
     *
     * @return Second of the two similar points or null
     */
    Point getCycleEnd();

    int getCycleEndPosition();

}
