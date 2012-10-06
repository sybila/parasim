package org.sybila.parasim.extension.visualisation.projection.model.points;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.util.Coordinate;
import org.sybila.parasim.visualisation.projection.api.layers.Layer;
import org.sybila.parasim.visualisation.projection.api.layers.Layers;
import org.sybila.parasim.visualisation.projection.api.points.PointLayers;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class LayersAdapter implements PointLayers {

    private Layers layers;

    public LayersAdapter(Layers target) {
        if (target == null) {
            throw new IllegalArgumentException("Layers are null.");
        }
        layers = target;
    }

    @Override
    public Layer get(int dimension, int index) {
        return layers.get(dimension, index);
    }

    @Override
    public Coordinate getCoordinate(Point target) {
        return layers.getCoordinate(target);
    }

    @Override
    public int getDimension() {
        return layers.getDimension();
    }

    @Override
    public float getFlatValue(int dimension) {
        return layers.getFlatValue(dimension);
    }

    @Override
    public int getNonFlatDimensionNumber() {
        return layers.getNonFlatDimensionNumber();
    }

    @Override
    public Point getPoint(Coordinate target) {
        return layers.getPoint(target);
    }

    @Override
    public int getSize(int dimension) {
        return layers.getSize(dimension);
    }

    @Override
    public boolean isFlat(int dimension) {
        return layers.isFlat(dimension);
    }
}
