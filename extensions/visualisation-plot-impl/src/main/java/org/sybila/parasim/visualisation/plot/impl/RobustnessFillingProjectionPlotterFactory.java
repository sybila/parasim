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
package org.sybila.parasim.visualisation.plot.impl;

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.AbstractVerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.api.PlotterFactory;
import org.sybila.parasim.visualisation.plot.impl.gui.ProjectionPlotter;
import org.sybila.parasim.visualisation.plot.impl.layer.EpsilonGridFactory;
import org.sybila.parasim.visualisation.plot.impl.layer.GridPointLayer;
import org.sybila.parasim.visualisation.plot.impl.layer.RobustnessTransformer;
import org.sybila.parasim.visualisation.plot.impl.render.ValidityPointRenderer;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated In hindsight, this approach has been proven wrong,
 * as robustness expresses maximum distance between trajectories, not between starting points.
 */
public class RobustnessFillingProjectionPlotterFactory implements PlotterFactory {

    private ResultPlotterConfiguration conf;

    public RobustnessFillingProjectionPlotterFactory(ResultPlotterConfiguration conf) {
        this.conf = conf;
    }

    public Plotter getPlotter(VerificationResult result, OdeSystem odeSystem) {
        if (result.size() < 1) {
            return new EmptyPlotter();
        }
        if (result.getPoint(0).getDimension() < 2) {
            return new OneDimensionalPlotter();
        }
        OrthogonalSpace extent = new SpaceUtils(conf).provideWithPadding(AbstractVerificationResult.getEncompassingSpace(result, odeSystem));
        return new ProjectionPlotter(conf, result, odeSystem,
                new GridPointLayer(result, extent, EpsilonGridFactory.getCoordinateFactory(conf), RobustnessTransformer.getFactory()),
                new ValidityPointRenderer(conf));
    }
}
