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
import org.sybila.parasim.model.space.OrthogonalSpaceImpl;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;
import org.sybila.parasim.visualisation.plot.impl.Point2DLayer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class IndependentPointLayer extends LayeredPointLayer implements LayerMetaFactory, LayerFactory, Point2DLayer {

    private VerificationResult result;
    private int xAxis, yAxis;
    private List<float[]> layer;
    private List<Layer>[] layers;

    public IndependentPointLayer(VerificationResult result, OrthogonalSpaceImpl bounds, Layering layering) {
        super(bounds);
        this.result = result;
        layers = layering.computeLayers(result, bounds);
        if (layers.length != bounds.getDimension()) {
            throw new IllegalArgumentException("Layering returned a wrong number of dimensions.");
        }
        layer = new ArrayList<float[]>();
    }

    @Override
    protected List<Layer> getLayers(int index) {
        return layers[index];
    }

    protected VerificationResult getResult() {
        return result;
    }

    @Override
    protected int getXAxis() {
        return xAxis;
    }

    @Override
    protected int getYAxis() {
        return yAxis;
    }

    //LayerMetaFactory//
    @Override
    public LayerFactory getLayerFactory(int xAxis, int yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;

        return this;
    }

    @Override
    public Point2DLayer getLayer(Map<Integer, Integer> projections) {
        layer.clear();

        //filter points//
        for (int i = 0; i < result.size(); i++) {
            Point p = result.getPoint(i);

            //has to be in bounds//
            if (!getBounds().isIn(p)) {
                continue;
            }

            //go through all layers//
            int dim;
            for (dim = 0; dim < getBounds().getDimension(); dim++) {
                if ((dim == xAxis) || (dim == yAxis)) { //leave out certain layers
                    continue;
                }
                if (!getLayers(dim).get(projections.get(dim)).isIn(p.getValue(dim))) {
                    break;
                }
            }
            if (dim == getBounds().getDimension()) {
                layer.add(new float[]{p.getValue(xAxis), p.getValue(yAxis), result.getRobustness(i).getValue()});
            }
        }

        return this;
    }

    //Point2DLayer
    @Override
    public int size() {
        return layer.size();
    }

    @Override
    public float getX(int index) {
        return layer.get(index)[0];
    }

    @Override
    public float getY(int index) {
        return layer.get(index)[1];
    }

    @Override
    public float robustness(int index) {
        return layer.get(index)[2];
    }
}
