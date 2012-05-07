/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.model.ode.DoubleMap;
import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.result.AbstractVerificationResult;
import org.sybila.parasim.model.verification.result.ArrayVerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.visualisation.plot.impl.layer.EpsilonGridFactory;
import org.sybila.parasim.visualisation.plot.impl.layer.GridPointLayer;
import org.sybila.parasim.visualisation.plot.impl.layer.WeightedFourNeighbourTransformer;
import org.sybila.parasim.visualisation.plot.impl.render.CirclePointRenderer;
import org.sybila.parasim.visualisation.plot.impl.render.ThreeColorPointRenderer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
@Deprecated
class TestVariableMapping extends DoubleMap<Integer> implements PointVariableMapping {

    public TestVariableMapping() {
        put(new Integer(0), "x");
        put(new Integer(1), "y");
        put(new Integer(2), "z");
    }

    @Override
    public int getDimension() {
        return 3;
    }

    /*
     * private static VerificationResult createResult() { Point[] points = new
     * Point[5]; points[0] = new ArrayPoint(0, 0f, 2.3f, 0f); points[1] = new
     * ArrayPoint(0, 3.5f, 4.3f, 0f); points[2] = new ArrayPoint(0, 5.1f, 0.6f,
     * 0.5f); points[3] = new ArrayPoint(0, 4.1f, 1.3f, 0.8f); points[4] = new
     * ArrayPoint(0, 1.8f, 3.8f, 1f); float[] robustness = new float[]{-2.5f,
     * 0.8f, 3.4f, 2.9f, -5.3f}; return new ArrayVerificationResult(5, points,
     * robustness); }
     */
    private static VerificationResult createResult() {
        float[] xCoords = new float[]{0f, 0.1f, 0.35f, 0.42f, 0.5f, 0.68f, 0.72f, 0.9f, 1f};
        float[] yCoords = new float[]{0f, 0.12f, 0.15f, 0.3f, 0.48f, 0.61f, 0.78f, 0.92f, 1f};
        float[] zCoords = new float[]{0f, 0.25f, 0.5f, 0.75f, 1f};

        int xDim = xCoords.length;
        int yDim = yCoords.length;
        int zDim = zCoords.length;

        List<Point> points = new ArrayList<Point>();
        List<Float> robustness = new ArrayList<Float>();

        final double prob = 0.4;

        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                for (int k = 0; k < zDim; k++) {
                    if (Math.random() < prob) {
                        points.add(new ArrayPoint(0, xCoords[i], yCoords[j], zCoords[k]));
                        robustness.add(xCoords[i] + yCoords[j] + zCoords[k] - 1.5f);
                    }
                }
            }
        }

        int dim = points.size();
        Point[] pointArray = new Point[dim];
        points.toArray(pointArray);
        Robustness[] robustArray = new Robustness[dim];
        for (int i = 0; i < dim; i++) {
            robustArray[i] = new SimpleRobustness(robustness.get(i));
        }

        return new ArrayVerificationResult(dim, pointArray, robustArray);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                VerificationResult result = createResult();
                OrthogonalSpace extent = AbstractVerificationResult.getEncompassingSpace(result);
                ResultPlotterConfiguration conf = new ResultPlotterConfiguration();
                Plotter test = new ProjectionPlotter(conf, result, new TestVariableMapping(),
                        new GridPointLayer(result, extent, EpsilonGridFactory.getCoordinateFactory(conf), WeightedFourNeighbourTransformer.getFactory()),
                        new ThreeColorPointRenderer(new CirclePointRenderer(3), conf, Color.GREEN, Color.RED, Color.BLUE));
                test.plot();
            }
        });

    }
}