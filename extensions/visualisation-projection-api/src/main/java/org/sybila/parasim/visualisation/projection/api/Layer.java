package org.sybila.parasim.visualisation.projection.api;

/**
 * An interval in an number axis. Has a reference value (centre) and is
 * able to decide whether a number fits inside.
 *
 * An implementation of layer should satisfy the following condition:
 * <code>isIn(getValue())</code>
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Layer {

    /**
     * Decide whether a number is inside this layer.
     * @param x tested number.
     * @return <code>true</code> when the number is in this layer, <code>false</code> otherwise.
     */
    public boolean isIn(float x);

    /**
     * Return reference value (center) of this layer.
     * @return Value on characterising this layer on the number axoi
     */
    public float getValue();

    /**
     * Compares two layers according to their reference values.
     */
    public static class Comparator implements java.util.Comparator<Layer> {

        @Override
        public int compare(Layer t1, Layer t2) {
            return Float.compare(t1.getValue(), t2.getValue());
        }
    }
}
