package org.sybila.parasim.visualisation.projection.api.points;

import org.sybila.parasim.visualisation.projection.api.layers.Layers;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface PointLayers extends Layers {

    public SingleLayer getSingleLayer(LayerSpecification specification);
}
