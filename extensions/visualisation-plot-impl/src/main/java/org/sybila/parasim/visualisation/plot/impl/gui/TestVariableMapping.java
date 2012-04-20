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

import org.sybila.parasim.visualisation.plot.impl.render.RGCirclePointRenderer;
import java.awt.EventQueue;
import org.sybila.parasim.model.ode.DoubleMap;
import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.AbstractVerificationResult;
import org.sybila.parasim.model.verification.result.ArrayVerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.visualisation.plot.impl.layer.CoordinateLayer;

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
        int dim = xDim * yDim * zDim;

        Point[] points = new Point[dim];
        float[] robustness = new float[dim];

        int p = 0;
        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                for (int k = 0; k < zDim; k++, p++) {
                    points[p] = new ArrayPoint(0, xCoords[i], yCoords[j], zCoords[k]);
                    robustness[p] = xCoords[i] + yCoords[j] + zCoords[k] - 1.5f;
                }
            }
        }
        return new ArrayVerificationResult(dim, points, robustness);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                VerificationResult result = createResult();
                OrthogonalSpace extent = AbstractVerificationResult.getEncompassingSpace(result);
                ResultPlotterConfiguration conf = new ResultPlotterConfiguration();
                Plotter test = new ProjectionPlotter(new ResultPlotterConfiguration(), result, new TestVariableMapping(), new CoordinateLayer(conf, result, extent), new RGCirclePointRenderer());
                test.plot();
            }
        });

    }
}