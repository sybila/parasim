package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.List;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class LayeredPointLayer extends OrthogonalBoundedPointLayer implements LayerFactory {

    public LayeredPointLayer(OrthogonalSpace bounds) {
        super(bounds);
    }

    protected abstract List<Layer> getLayers(int dim);

    @Override
    public int ticks(int index) {
        return getLayers(index).size();
    }

    @Override
    public int getTicks(int index, float value) {
        List<Layer> list = getLayers(index);

        //see if it is in layer//
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isIn(value)) {
                return i;
            }
        }

        //is before//
        if (value < list.get(0).getValue()) {
            return 0;
        }

        //is between or at the end//
        int i = 1;
        while ((i < list.size()) && (value < list.get(i).getValue())) {
            i++;
        }
        return i - 1;
    }

    @Override
    public float getValue(int index, int ticks) {
        return getLayers(index).get(ticks).getValue();
    }
}
