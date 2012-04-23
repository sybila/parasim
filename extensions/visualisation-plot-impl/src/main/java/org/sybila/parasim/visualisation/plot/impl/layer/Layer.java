package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface Layer {

    public boolean isIn(float x);

    public float getValue();

    public static class Comparator implements java.util.Comparator<Layer> {

        public int compare(Layer t, Layer t1) {
            return Float.compare(t.getValue(), t1.getValue());
        }
    }
}
