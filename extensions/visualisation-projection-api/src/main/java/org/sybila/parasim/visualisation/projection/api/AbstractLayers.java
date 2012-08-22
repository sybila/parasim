package org.sybila.parasim.visualisation.projection.api;

import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.util.Coordinate;

/**
 *
 * Provides basic implementation of {@link Layers}. All methods are implemented
 * on basis of: <ul> <li>{@link #getDimensionImpl()}</li> <li>{@link #getSizeImpl(int)}</li> <li>{@link #getImpl(int, int)</li>
 * </ul>
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class AbstractLayers implements Layers {

    /**
     * Get the number of dimension (axes).
     */
    protected abstract int getDimensionImpl();

    /**
     * Get the number of layers in given dimension. Always should be positive.
     */
    protected abstract int getSizeImpl(int dimension);

    /**
     * Get the layer in given dimension with given index.
     */
    protected abstract Layer getImpl(int dimension, int index);

    @Override
    public int getDimension() {
        return getDimensionImpl();
    }

    @Override
    public int getSize(int dimension) {
        return getSizeImpl(dimension);
    }

    @Override
    public Layer get(int dimension, int index) {
        return getImpl(dimension, index);
    }

    private boolean isFlatImpl(int dimension) {
        return getSizeImpl(dimension) == 1;
    }

    @Override
    public boolean isFlat(int dimension) {
        return isFlatImpl(dimension);
    }

    @Override
    public float getFlatValue(int dimension) {
        if (!isFlatImpl(dimension)) {
            throw new IllegalArgumentException("Given dimension is not flat.");
        }
        return getImpl(dimension, 0).getValue();
    }

    @Override
    public int getNonFlatDimensionNumber() {
        int dim = getDimensionImpl();
        int num = dim;
        for (int i = 0; i < dim; i++) {
            if (isFlatImpl(i)) {
                num--;
            }
        }
        return num;
    }

    @Override
    public Point getPoint(Coordinate target) {
        if (target == null) {
            throw new IllegalArgumentException("Argument (Coordinate) cannot be null.");
        }
        int dim = getDimensionImpl();
        // dimension match //
        if (target.getDimension() != dim) {
            throw new IllegalArgumentException("Dimension of Layers and Coordinate do not match.");
        }
        float[] values = new float[dim];
        for (int i = 0; i < dim; i++) {
            values[i] = getImpl(i, target.getCoordinate(i)).getValue();
        }
        return new ArrayPoint(0, values, 0, dim);
    }

    private Integer getCoordinate(int dim, float value, int start, int end) {
        if (start + 1 == end) {
            boolean inStart = getImpl(dim, start).isIn(value);
            boolean inEnd = getImpl(dim, end).isIn(value);
            if (inStart && inEnd) {
                throw new IllegalStateException("Two layers overlap.");
            } else if (inStart) {
                return start;
            } else if (inEnd) {
                return end;
            } else {
                return null;
            }
        }
        int mid = (start + end) / 2;
        float midVal = getImpl(dim, mid).getValue();
        if (value < midVal) {
            return getCoordinate(dim, value, start, mid);
        } else {
            return getCoordinate(dim, value, mid, end);
        }
    }

    @Override
    public Coordinate getCoordinate(Point target) {
        if (target == null) {
            throw new IllegalArgumentException("Argument (Point) cannot be null.");
        }
        int dim = getDimensionImpl();
        // dimensions match //
        if (target.getDimension() != dim) {
            throw new IllegalArgumentException("Dimensions of Layers and Point do not match.");
        }
        Coordinate.Builder builder = new Coordinate.Builder(dim);
        for (int i = 0; i < dim; i++) {
            float value = target.getValue(i);
            if (isFlatImpl(i)) {
                // Flat dimension //
                if (!getImpl(i, 0).isIn(value)) {
                    return null;
                }
                builder.setCoordinate(i, 0);

            } else {
                // non-flat dimension //
                Integer res = getCoordinate(i, value, 0, getSizeImpl(i) - 1);
                if (res == null) {
                    return null;
                }
                builder.setCoordinate(i, res);
            }
        }
        return builder.create();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder("Layers[");
        int dim = getDimensionImpl();
        if (dim != 0) {
            out.append(getSizeImpl(0));
            for (int i = 1; i < dim - 1; i++) {
                out.append(" ,");
                out.append(getSizeImpl(i));
            }
        }
        out.append("]");
        return out.toString();
    }
}