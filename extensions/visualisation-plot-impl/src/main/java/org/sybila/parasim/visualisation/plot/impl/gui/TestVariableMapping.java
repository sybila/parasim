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
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.ode.SimpleOdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.PointWithNeigborhoodWrapper;
import org.sybila.parasim.model.trajectory.PointWithNeighborhood;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.SimpleRobustness;
import org.sybila.parasim.model.verification.result.AbstractVerificationResult;
import org.sybila.parasim.model.verification.result.ArrayVerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;
import org.sybila.parasim.visualisation.plot.impl.layer.EpsilonGridFactory;
import org.sybila.parasim.visualisation.plot.impl.layer.GridPointLayer;
import org.sybila.parasim.visualisation.plot.impl.layer.RobustnessTransformer;
import org.sybila.parasim.visualisation.plot.impl.render.CirclePointRenderer;
import org.sybila.parasim.visualisation.plot.impl.render.ThreeColorPointRenderer;

/**
 * Testing class. Creates a verification result and displays it.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated Used only for testing purposes.
 */
@Deprecated
class TestVariableMapping {


    private static OdeSystem createOdeSystem() {
        Collection<OdeSystemVariable> vars = new ArrayList<>();
        int index = 0;
        for (String name: new String[] {"x", "y", "z"}) {
            vars.add(new OdeSystemVariable(name, index, new Constant(index)));
            index++;
        }
        return new SimpleOdeSystem(vars, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
    }

    private static VerificationResult createResult() {
        float[] xCoords = new float[]{0f, 0.1f, 0.35f, 0.42f, 0.5f, 0.68f, 0.72f, 0.9f, 1f};
        float[] yCoords = new float[]{0f, 0.12f, 0.15f, 0.3f, 0.48f, 0.61f, 0.78f, 0.92f, 1f};
        float[] zCoords = new float[]{0f, 0.25f, 0.5f, 0.75f, 1f};

        int xDim = xCoords.length;
        int yDim = yCoords.length;
        int zDim = zCoords.length;

        List<PointWithNeighborhood> points = new ArrayList<>();
        List<Float> robustness = new ArrayList<>();

        final double prob = 0.4;

        for (int i = 0; i < xDim; i++) {
            for (int j = 0; j < yDim; j++) {
                for (int k = 0; k < zDim; k++) {
                    if (Math.random() < prob) {
                        points.add(new PointWithNeigborhoodWrapper(new ArrayPoint(0, xCoords[i], yCoords[j], zCoords[k])));
                        robustness.add(xCoords[i] + yCoords[j] + zCoords[k] - 1.5f);
                    }
                }
            }
        }

        int dim = points.size();
        PointWithNeighborhood[] pointArray = new PointWithNeighborhood[dim];
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
                OrthogonalSpace extent = AbstractVerificationResult.getEncompassingSpace(result, createOdeSystem());
                ResultPlotterConfiguration conf = new ResultPlotterConfiguration();
                Plotter test = new ProjectionPlotter(conf, result, createOdeSystem(),
                        new GridPointLayer(result, extent, EpsilonGridFactory.getCoordinateFactory(conf), RobustnessTransformer.getFactory()),
                        new ThreeColorPointRenderer(new CirclePointRenderer(3), conf, Color.GREEN, Color.RED, Color.BLUE));
                test.plot();
            }
        });

    }
}