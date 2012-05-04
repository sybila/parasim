package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.visualisation.plot.impl.layer.utils.LayeredGrid;
import org.sybila.parasim.visualisation.plot.impl.layer.utils.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FourNeighbourhoodTransformer extends NeighbourhoodTransformer<SingleLayerFourNeighbourhood> {

    public FourNeighbourhoodTransformer(Float[][] target, int xSize, int ySize) {
        super(target, xSize, ySize, SingleLayerFourNeighbourhood.getComparator());
    }

    public static GridPointLayer.SingleLayerFactory getFactory() {
        return new Factory();
    }

    @Override
    protected SingleLayerFourNeighbourhood getNeighbourhood(int x, int y) {
        return new SingleLayerFourNeighbourhood(
                getRobustness(x - 1, y) != null,
                getRobustness(x, y - 1) != null,
                getRobustness(x + 1, y) != null,
                getRobustness(x, y + 1) != null);
    }

    @Override
    protected List<Pair<Integer, Integer>> getNeighbours(int x, int y) {
        List<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
        result.add(new Pair<Integer, Integer>(x - 1, y));
        result.add(new Pair<Integer, Integer>(x + 1, y));
        result.add(new Pair<Integer, Integer>(x, y - 1));
        result.add(new Pair<Integer, Integer>(x, y + 1));
        return result;
    }

    @Override
    protected float computeRobustnes(int x, int y, SingleLayerFourNeighbourhood neighbourhood) {
        float result = 0;
        int size = 0;
        for (Pair<Integer, Integer> neigh : getNeighbours(x, y)) {
            Float rob = getRobustness(neigh.first(), neigh.second());
            if (rob != null) {
                result += rob;
                size++;
            }
        }
        return result / size;
    }

    private static class Factory implements GridPointLayer.SingleLayerFactory {

        private SimpleSingleLayerFactory init = new SimpleSingleLayerFactory();

        public void transform(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections) {
            init.transform(target, source, xAxis, yAxis, xSize, ySize, projections);
            FourNeighbourhoodTransformer trans = new FourNeighbourhoodTransformer(target, xSize, ySize);
            trans.transform();
        }
    }
}
