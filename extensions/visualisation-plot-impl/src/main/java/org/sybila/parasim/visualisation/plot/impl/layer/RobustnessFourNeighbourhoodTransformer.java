/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
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
 * Only when given point is "covered" by all four neighbours is it robust.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class RobustnessFourNeighbourhoodTransformer extends FourNeighbourhoodTransformer {
    private int xAxis, yAxis;
    private LayeredGrid<Float> source;
    private Coordinate.Builder coordinate;

    protected RobustnessFourNeighbourhoodTransformer(Float[][] target, int xAxis, int yAxis, int xSize, int ySize, LayeredGrid<Float> source, Map<Integer,Integer> projections) {
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
        if (neighbourhood.size() == 4) {
            float sum = 0;
            Coordinate center = coordinate.setCoordinate(xAxis, x).setCoordinate(yAxis, y).create();
            for (Pair<Integer, Integer> neigh : getNeighbours(x, y)) {
                Coordinate point = coordinate.setCoordinate(xAxis, neigh.first()).setCoordinate(yAxis, neigh.second()).create();
                float distance = new EuclideanMetric().distance(source.getPoint(center), source.getPoint(point)).value();
                float robustness = getRobustness(neigh.first(), neigh.second());

                if (distance > robustness) {
                    return 0;
                } else {
                    sum += robustness;
                }
            }
            return sum/4;
        } else {
            return 0;
        }
    }

    public static class Factory implements GridPointLayer.SingleLayerFactory {
        private SimpleSingleLayerFactory init = new SimpleSingleLayerFactory();

        @Override
        public void transform(Float[][] target, LayeredGrid<Float> source, int xAxis, int yAxis, int xSize, int ySize, Map<Integer, Integer> projections) {
            init.transform(target, source, xAxis, yAxis, xSize, ySize, projections);
            RobustnessFourNeighbourhoodTransformer trans = new RobustnessFourNeighbourhoodTransformer(target, xAxis, yAxis, xSize, ySize, source, projections);
            trans.transform();
        }

    }

}
