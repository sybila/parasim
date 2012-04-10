package org.sybila.parasim.visualisation.plot.impl;

/**
 * Contains points with associated robustness values whose coordinates are
 * projected into 2D.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Point2DLayer {

    /**
     * Return number of contained points.
     *
     * @return Number of contained points.
     */
    public int size();

    /**
     * Return x coordinate of a given point.
     *
     * @param index Index of a point.
     * @return X coordinate of the point.
     */
    public float getX(int index);

    /**
     * Return y coordinate of a given point.
     *
     * @param index Index of a point.
     * @return Y coordinate of the point.
     */
    public float getY(int index);

    /**
     * Return robustness value associated with a given point.
     *
     * @param index Index of a point.
     * @return Robustness value of the point.
     */
    public float robustness(int index);

    /**
     * Return a lower bound of x coordinates.
     *
     * @return Lower bound of x coordinates.
     */
    public float minX();

    /**
     * Return a lower bound of y coordinates.
     *
     * @return Lower bound of y coordinates.
     */
    public float minY();

    /**
     * Return an upper bound of x coordinates.
     *
     * @return Upper bound of x coordinates.
     */
    public float maxX();

    /**
     * Return an upper bound of y coordinates.
     *
     * @return Upper bound of y coordinates.
     */
    public float maxY();
}
