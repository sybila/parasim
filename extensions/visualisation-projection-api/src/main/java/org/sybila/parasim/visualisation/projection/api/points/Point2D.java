package org.sybila.parasim.visualisation.projection.api.points;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public final class Point2D {

    private final float x, y;

    private static void checkFloat(float x) {
        if (Float.isNaN(x)) {
            throw new IllegalArgumentException("Point2D does not support NaN.");
        } else if (Float.isInfinite(x)) {
            throw new IllegalArgumentException("Point2D does not support infinite values.");
        }
    }

    public Point2D(float x, float y) {
        checkFloat(x);
        checkFloat(y);
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Point2D)) {
            return false;
        }
        Point2D target = (Point2D) obj;
        return (Float.floatToIntBits(getX()) == Float.floatToIntBits(target.getX()))
                && (Float.floatToIntBits(getY()) == Float.floatToIntBits(target.getY()));
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(getX()) * 53 + Float.floatToIntBits(getY());
    }
}
