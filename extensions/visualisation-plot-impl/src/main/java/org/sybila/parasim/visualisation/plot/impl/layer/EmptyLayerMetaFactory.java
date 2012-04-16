package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EmptyLayerMetaFactory implements LayerMetaFactory {

    public LayerFactory getLayerFactory(int xAxis, int yAxis) {
        return new EmptyLayerFactory();
    }

}
