
package parasim.computation.simulation;

/**
 * Stores points of one Trajectory and enables new seqeunces to be appended
 * as they are being computed.
 * @author sven
 */
public interface Trajectory extends Iterable<Point> {

    /**
     * @return Number of dimensions of each point of the trajectory.
     */
    int getDimension();

    /**
     * @return The last point of the trajectory to be yet computed, in case
     * of a cycle the turn around point is returned.
     */
    Point getLastPoint();

    /**
     * @return The total number of points of the trajectory yet computed.
     */
    int getLength();

    /**
     * @param index
     * @return true if trajectory contains point with such index
     */
    boolean hasPoint(int index);

    /**
     * Returns the point with specified position from this trajectory.
     *
     * @param index position on trajectory
     * @return
     */
    Point getPoint(int index);    

    /**
     * @return The point index of the first point on the trajectory.
     */
    int getFirstPointIndex();

    /**
     * Iterator to iterate over trajectory points.
     * @return point iterator
     */
    TrajectoryIterator iterator();

    /**
     * Iterates over points of the trajectory starting from point with given index.
     * Complexity of next() is constant.
     *
     * @param index Starting point of iteration.
     * @return Iterator with next() pointing to the point with specified index
     *         on success.
     */
    TrajectoryIterator iterator(int index);

    /**
     * Appends the given trajectory to the end of this one.
     * The operation takes place only if the current last point's position
     * is 1 less than the newly added trajectory's first point's position.
     *
     * @param tb TrajectoryBlock to be appended
     * @return true on success, false otherwise
     */
    boolean append(Trajectory trajectory);
}

