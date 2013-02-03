/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.visualisation.plot.impl.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FourNeighbourhoodTransformer extends NeighbourhoodTransformer<SingleLayerFourNeighbourhood> {

    protected FourNeighbourhoodTransformer(Float[][] target, int xSize, int ySize) {
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

        @Override
        public void transform(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections) {
            init.transform(target, source, xAxis, yAxis, xSize, ySize, projections);
            FourNeighbourhoodTransformer trans = new FourNeighbourhoodTransformer(target, xSize, ySize);
            trans.transform();
        }
    }
}
