package org.sybila.parasim.visualisation.plot.impl.layer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class IntervalLayer implements IndependentLayer.Layer {

    private float center, epsilon;

        public IntervalLayer(float center, float epsilon) {
            this.center = center;
            this.epsilon = epsilon;
        }

        public boolean isIn(float x) {
            return (Math.abs(center - x) < epsilon);
        }

        public float getValue() {
            return center;
        }
}
