
package org.sybila.parasim.model.trajectory;

import java.util.Iterator;

/**
 * Stores points of one trajectory.
 * 
 * @author <a href="mailto:xdrazan@fi.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
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
     * Iterates over points of the trajectory starting from point with given index.
     * Complexity of next() is constant.
     *
     * @param index Starting point of iteration.
     * @return Iterator with next() pointing to the point with specified index
     *         on success.
     */
    Iterator<Point> iterator(int index);


}

