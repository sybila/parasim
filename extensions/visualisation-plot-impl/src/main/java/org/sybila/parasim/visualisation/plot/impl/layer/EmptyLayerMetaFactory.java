package org.sybila.parasim.visualisation.plot.impl.layer;

import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;

/**
 *
 * @deprecated bogus class
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Deprecated
public class EmptyLayerMetaFactory implements LayerMetaFactory {

    @Override
    public LayerFactory getLayerFactory(int xAxis, int yAxis) {
        return new EmptyLayerFactory();
    }

}
