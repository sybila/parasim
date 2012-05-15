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