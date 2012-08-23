package org.sybila.parasim.visualisation.projection.api;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.util.Coordinate;

/**
 * List of layers along axes in N dimensions. Layers in one dimension should
 * always be sortyed (in ascending order) with respect to their centers and
 * should not overlap.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Layers {

    /**
     * Get the number of dimension (axes).
     */
    public int getDimension();

    /**
     * Get the number of layers in given dimension.
     * Always should be positive.
     */
    public int getSize(int dimension);

    /**
     * Get the layer in given dimension with given index.
     */
    public Layer get(int dimension, int index);


    /**
     * Get the number of dimensions which are not flat (i.e. they have more than one layer).
     */
    public int getNonFlatDimensionNumber();

    /**
     * Decide whether given dimension is flat.
     * @return <code>true</code> when there is only one layer in given dimension, <code>false</code> otherwise.
     */
    public boolean isFlat(int dimension);

    /**
     * When the given dimension is flat, return the value of its layer.
     * @throws IllegalArgumentException when the given dimension is not flat.
     */
    public float getFlatValue(int dimension);


    /**
     * Get coordinate of a point in this dimensions.
     * @return Coordinate <code>coord</code>, such that each non-zero <code>n</code> lesser than
     * the number of dimensions satisfies <code>get(n, coord.getCoordinate(n)).isIn(target.getValue(n))</code>.
     * <code>null</code> when there is no such coordinate.
     * @see Point#getValue(int)
     * @see Coordinate#getCoordinate(int)
     */
    public Coordinate getCoordinate(Point target);

    /**
     * Get reference point from a coordinate.
     * @return Point <code>point</code> such that each non-zero <code>n</code> lesser than
     * the number of dimensions satisfies <code>point.getValue(n) == get(n, target.getCoordinate(n)).getValue()</code>.
     * @see Point#getValue(int)
     * @see Coordinate#getCoordinate(int)
     */
    public Point getPoint(Coordinate target);
}
