package org.sybila.parasim.model.trajectory;

/**
 * Stores points of one trajectory.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Trajectory extends Iterable<Point>
{

    /**
     * Returns number of dimensions of each point of the trajectory
     * 
     * @return Number of dimensions of each point of the trajectory.
     */
    int getDimension();

    /**
     * Returns the first point of the trajectory to be yet computed.
     * 
     * @return The first point of the trajectory to be yet computed.
     */
    Point getFirstPoint();

    /**
     * Returns the last point of the trajectory to be yet computed.
     * 
     * @return The last point of the trajectory to be yet computed.
     */
    Point getLastPoint();

    /**
     * Returns the total number of points of the trajectory yet computed.
     * 
     * @return The total number of points of the trajectory yet computed.
     */
    int getLength();

    /**
     * Returns the point with specified position from this trajectory.
     *
     * @param index position on trajectory
     * @return the point with the specified position
     */
    Point getPoint(int index);

    /**
     * Returns the reference pointed to this trajectory
     * 
     * @return reference pointed to this trajectory
     */
    TrajectoryReference getReference();
       
    /**
     * Checks whether the trajectory contains the point on the specified
     * position.
     * 
     * @param index
     * @return true if trajectory contains point with such index
     */
    boolean hasPoint(int index);

    /**
     * Iterates over points of the trajectory starting from the first point.
     * Complexity of next() is constant.
     *
     * @return Iterator with next() pointing to the first point of the trajectory.
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
}