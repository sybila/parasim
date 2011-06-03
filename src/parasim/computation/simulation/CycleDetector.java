
package parasim.computation.simulation;

/**
 * Enables cycle detecion on a trajectory.
 *
 * @author sven
 */
public interface CycleDetector {

    /**
     * Detects whether the newPoint is not a cycle back loop.
     *
     * @param newPoint Point to be examined for cycle loopback.
     * @return The point similar to newPoint detected as the loopback position
     *         of the cycle on success or NULL on failure.
     */
    Point detectCycle(Point newPoint);

}
