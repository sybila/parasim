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
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 * Projection algorithm which renders all points projected into 2D by omitting
 * other coordinates. Whether the point is rendered is independent on positions
 * on unprojected axes.
 *
 * Contains verification result.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OverlapPointLayer extends OrthogonalBoundedPointLayer implements Point2DLayer, LayerFactory, LayerMetaFactory {

    private VerificationResult src;
    private int xAxis, yAxis;

    /**
     * Initialize contained verification result and bounding space.
     *
     * @param source Verification result.
     * @param bounds Bounding space.
     */
    public OverlapPointLayer(VerificationResult source, OrthogonalSpaceImpl bounds) {
        super(bounds);
        src = source;
    }

    //LayerMetaFactory
    @Override
    public LayerFactory getLayerFactory(int xAxis, int yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;

        return this;
    }

    //LayerFactory
    @Override
    public Point2DLayer getLayer(Map<Integer, Integer> projections) {
        return this;
    }

    @Override
    public int ticks(int index) {
        return 10;
    }

    @Override
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

    @Override
    public float getValue(int index, int ticks) {
        return ticks;
    }

    //Point2DLayer
    @Override
    public int size() {
        return src.size();
    }

    @Override
    public float getX(int index) {
        return src.getPoint(index).getValue(xAxis);
    }

    @Override
    public float getY(int index) {
        return src.getPoint(index).getValue(yAxis);
    }

    @Override
    public float robustness(int index) {
        return src.getRobustness(index).getValue();
    }

    @Override
    protected int getXAxis() {
        return xAxis;
    }

    @Override
    protected int getYAxis() {
        return yAxis;
    }
}
