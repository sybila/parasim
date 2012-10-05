package org.sybila.parasim.extension.visualisation.projection.model.layers;

import org.sybila.parasim.visualisation.projection.api.layers.Layer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class NeighbourhoodLayer implements Layer {

    private float value, radius;

    private NeighbourhoodLayer(float value, float radius) {
        this.value = value;
        this.radius = radius;
    }

    @Override
    public boolean isIn(float x) {
        return Math.abs(x - value) <= radius;
    }

    @Override
    public float getValue() {
        return value;
    }

    public static Layer getInstance(float center, float radius) {
        if (Float.isNaN(center)) {
            throw new IllegalArgumentException("Center is not a number.");
        }
        if (Float.isNaN(radius)) {
            throw new IllegalArgumentException("Radius is not a number-");
        }
        if (Float.isInfinite(center)) {
            throw new IllegalArgumentException("Center is not finite.");
        }
        if (Float.isInfinite(radius)) {
            throw new IllegalArgumentException("Radius is not finite.");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius is lesser than or equal to zero.");
        }
        return new NeighbourhoodLayer(center, radius);
    }

    public static Layer getFromInterval(float lower, float upper) {
        if (Float.isNaN(upper)) {
            throw new IllegalArgumentException("Upper bound is not a number.");
        }
        if (Float.isNaN(lower)) {
            throw new IllegalArgumentException("Lower bound is not a number");
        }
        float delta = upper - lower;
        if (Float.isInfinite(delta)) {
            throw new IllegalArgumentException("NeighbourhoodLayer does not support infitite intervals.");
        }
        if (delta <= 0) {
            throw new IllegalArgumentException("Lower bound is greater than or equal to upper bound.");
        }
        return new NeighbourhoodLayer(lower + delta / 2, delta / 2);
    }
}
