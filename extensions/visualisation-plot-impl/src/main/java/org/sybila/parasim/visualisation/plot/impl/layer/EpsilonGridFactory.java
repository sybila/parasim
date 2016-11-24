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

import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.util.Block;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class EpsilonGridFactory implements GridPointLayer.GridFactory {

    private EpsilonLayering layering;

    public EpsilonGridFactory(float epsilon) {
        layering = new EpsilonLayering(epsilon);
    }

    @Override
    public LayeredGrid<Float> getGrid(VerificationResult result, OrthogonalSpace bounds) {
        Block.Builder<Layer> layers = new Block.Builder<Layer>(layering.computeLayers(result, bounds));
        LayeredGrid<Float> target = new LayeredGrid<Float>(layers.create());

        for (int i = 0; i < result.size(); i++) {
            target.put(target.getCoordinate(result.getPoint(i)), result.getRobustness(i).getValue());

        }

        return target;
    }

    public static EpsilonGridFactory getCoordinateFactory(ResultPlotterConfiguration conf) {
        return new EpsilonGridFactory(conf.getMinimumDifference());
    }

}
