package org.sybila.parasim.model.verification.result;

import org.sybila.parasim.model.trajectory.Point;

/**
 * Stores points and their associated robustness values in an array.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ArrayVerificationResult extends AbstractVerificationResult {

    private int size;
    private Point[] points;
    private float[] robustness;

    /**
     * Creates new verification result with specified contents. Note: arguments are not copied.
     * @param size Number of points.
     * @param points Array containing points.
     * @param robustness Array containing robustness values.
     * @throws IllegalArgumentException when lengths of given arrays do not match.
     */
    public ArrayVerificationResult(int size, Point[] points, float[] robustness) {
        if ((size != points.length) || (size != robustness.length)) {
            throw new IllegalArgumentException("Lengths of points and robustness arrays have to match the size argument.");
        }
        this.size = size;
        this.points = points;
        this.robustness = robustness;
    }

    @Override
    public Point getPoint(int index) {
        return points[index];
    }

    @Override
    public float getRobustness(int index) {
        return robustness[index];
    }

    @Override
    public int size() {
        return size;
    }
}
