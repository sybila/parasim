package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.Map;
import org.sybila.parasim.util.Coordinate;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimpleSingleLayerFactory implements GridPointLayer.SingleLayerFactory {

    public void transform(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections) {
        int dim = source.getDimension();
        Coordinate.Builder coord = new Coordinate.Builder(dim);
        for (int i = 0; i < dim; i++) {
            if (i == xAxis || i == yAxis) {
                continue;
            }
            coord.setCoordinate(i, projections.get(i));
        }

        for (int i = 0; i < xSize; i++) {
            coord.setCoordinate(xAxis, i);
            for (int j = 0; j < ySize; j++) {
                coord.setCoordinate(yAxis, j);

                target[i][j] = source.get(coord.create());
            }
        }
    }


}