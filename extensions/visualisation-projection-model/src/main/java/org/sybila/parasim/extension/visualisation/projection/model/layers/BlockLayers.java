package org.sybila.parasim.extension.visualisation.projection.model.layers;

import org.sybila.parasim.util.Block;
import org.sybila.parasim.visualisation.projection.api.layers.AbstractLayers;
import org.sybila.parasim.visualisation.projection.api.layers.Layer;
import org.sybila.parasim.visualisation.projection.api.layers.Layers;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class BlockLayers extends AbstractLayers implements Layers {

    private Block<Layer> layers;

    public BlockLayers(Block<Layer> layers) {
        this.layers = layers;
    }

    @Override
    protected int getDimensionImpl() {
        return layers.getDimension();
    }

    @Override
    protected int getSizeImpl(int dimension) {
        return layers.getSize(dimension);
    }

    @Override
    protected Layer getImpl(int dimension, int index) {
        return layers.get(dimension, index);
    }
}
