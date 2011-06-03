
package parasim.computation;

import java.util.Iterator;

/**
 * Stores points of one trajectory.
 * @author sven
 */
public interface Trajectory extends Iterable<Point> {

    /**
     * @return Number of dimensions of each point of the trajectory.
     */
    int getDimension();

    /**
     * @return The first point of the trajectory to be yet computed.
     */
    Point getFirstPoint();

    /**
     * @return The last point of the trajectory to be yet computed.
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
     * Iterator to iterate over trajectory points.
     * @return point iterator
     */
    Iterator<Point> iterator();

//	/**
//     * Iterates over points of the trajectory starting from point with given index.
//     * Complexity of next() is constant.
//     *
//     * @param index Starting point of iteration.
//     * @return Iterator with next() pointing to the point with specified index
//     *         on success.
//     */
//    Iterator<Point> iterator(int index);
//
//    /**
//     * Appends the given trajectory to the end of this one.
//     * The operation takes place only if the current last point's position
//     * is 1 less than the newly added trajectory's first point's position.
//     *
//     * @param tb TrajectoryBlock to be appended
//     * @return true on success, false otherwise
//     */
//    boolean append(Trajectory trajectory);

}

