package org.sybila.parasim.model.verification.stlstar;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Structure containing multiple points -- used to contain values for given time
 * and for all frozen times. Individual cannot be null.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface MultiPoint {

    /**
     * Returns the number of point contained in this structure.
     *
     * @return Number of points.
     */
    int getDimension();

    /**
     * Returns single point from this structure. Should throw an exception when
     * index is out of bounds.
     *
     * @param index Index of the point.
     * @return Point of given index.
     */
    Point getPoint(int index);
}
