/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

import java.util.Map;
import org.sybila.parasim.model.trajectory.EuclideanMetric;
import org.sybila.parasim.util.Coordinate;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class WeightedFourNeighbourTransformer extends FourNeighbourhoodTransformer {

    private int xAxis, yAxis;
    private LayeredGrid<Float> source;
    private Coordinate.Builder coordinate;

    public WeightedFourNeighbourTransformer(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections) {
        super(target, xSize, ySize);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.source = source;

        // move projections to coordinate builder //
        int dim = source.getDimension();
        coordinate = new Coordinate.Builder(dim);
        for (int i = 0; i < dim; i++) {
            if (i != xAxis && i != yAxis) {
                coordinate.setCoordinate(i, projections.get(i));
            }
        }
    }

    public static GridPointLayer.SingleLayerFactory getFactory() {
        return new Factory();
    }

    @Override
    protected float computeRobustnes(int x, int y, SingleLayerFourNeighbourhood neighbourhood) {
        Coordinate center = coordinate.setCoordinate(xAxis, x).setCoordinate(yAxis, y).create();

        float sum = 0;
        float dSum = 0;
        for (Pair<Integer, Integer> neigh : getNeighbours(x, y)) {
            Float robustness = getRobustness(neigh.first(), neigh.second());
            if (robustness != null) {
                Coordinate point = coordinate.setCoordinate(xAxis, neigh.first()).setCoordinate(yAxis, neigh.second()).create();
                float distance = new EuclideanMetric().distance(source.getPoint(center), source.getPoint(point)).value();
                sum += robustness * distance;
                dSum += distance;
            }
        }
        return sum / dSum;
    }

    public static class Factory implements GridPointLayer.SingleLayerFactory {

        private SimpleSingleLayerFactory init = new SimpleSingleLayerFactory();

        public void transform(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections) {
            init.transform(target, source, xAxis, yAxis, xSize, ySize, projections);
            WeightedFourNeighbourTransformer trans = new WeightedFourNeighbourTransformer(target, source, xAxis, yAxis, xSize, ySize, projections);
            trans.transform();
        }
    }
}
