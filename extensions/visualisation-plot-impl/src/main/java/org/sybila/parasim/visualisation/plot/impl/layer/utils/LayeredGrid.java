package org.sybila.parasim.visualisation.plot.impl.layer.utils;

import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.visualisation.plot.impl.layer.Layer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class LayeredGrid<T> extends Grid<T> {

    private Block<Layer> layers;

    public LayeredGrid(Block<Layer> layers) {
        super(layers.getDimensions());
        this.layers = layers;
    }

    public Block<Layer> getLayers() {
        return layers;
    }

    public Point getPoint(Coordinate coord) {
        checkDimension(coord);
        int dim = coord.getDimension();
        float [] point = new float[dim];
        for (int i = 0; i < dim; i++) {
            point[i] = layers.get(dim, coord.getCoordinate(i)).getValue();
        }
        return new ArrayPoint(0, point, 0, dim);
    }

    public float getLayerValue(int dimension, int index) {
        return getLayers().get(dimension, index).getValue();
    }


}
