package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.Map;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EmptyLayerFactory implements LayerFactory {

    @Override
    public Point2DLayer getLayer(Map<Integer, Integer> projections) {
        return new EmptyLayer();
    }

    @Override
    public int ticks(int index) {
        return 10;
    }

    @Override
    public float getValue(int index, int ticks) {
        return ticks;
    }

    public int getTicks(int index, float value) {
        int val = Math.round(value);
        if (val < 0) {
            return 0;
        }
        if (val > 10) {
            return 10;
        }
        return val;
    }




}
