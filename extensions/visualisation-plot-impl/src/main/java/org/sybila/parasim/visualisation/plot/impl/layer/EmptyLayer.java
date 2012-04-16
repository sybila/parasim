package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EmptyLayer implements Point2DLayer {

    @Override
    public int size() {
        return 0;
    }

    @Override
    public float getX(int index) {
        throw new IllegalArgumentException("Index out of bounds.");
    }

    @Override
    public float getY(int index) {
        throw new IllegalArgumentException("Index out of bounds.");
    }

    public float robustness(int index) {
        throw new IllegalArgumentException("Index out of bounds.");
    }

    @Override
    public float maxX() {
        return 1;
    }

    @Override
    public float minX() {
        return 0;
    }

    @Override
    public float maxY() {
        return 1;
    }

    @Override
    public float minY() {
        return 0;
    }
}
