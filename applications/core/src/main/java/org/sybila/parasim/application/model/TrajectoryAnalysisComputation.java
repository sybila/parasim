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
package org.sybila.parasim.application.model;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.annotations.RunWith;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.octave.OctaveSimulationEngine;
import org.sybila.parasim.computation.simulation.octave.OctaveSimulationEngineFactory;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.api.STLVerifier;
import org.sybila.parasim.core.annotations.Inject;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.execution.api.SharedMemoryExecutor;
import org.sybila.parasim.execution.api.annotations.NumberOfInstances;
import org.sybila.parasim.model.Mergeable;
import org.sybila.parasim.model.Mergeable.Void;
import org.sybila.parasim.model.computation.AbstractComputation;
import org.sybila.parasim.model.computation.Computation;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.stl.Formula;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.api.PlotterWindowListener;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@RunWith(executor=SharedMemoryExecutor.class)
@NumberOfInstances(1)
public class TrajectoryAnalysisComputation extends AbstractComputation<Mergeable.Void> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrajectoryAnalysisComputation.class);

    @Provide
    private final OdeSystem odeSystem;
    @Provide
    private final PrecisionConfiguration precision;
    @Provide
    private final Point point;
    @Provide
    private final OrthogonalSpace simulationSpace;
    private final Plotter plotter;
    private final Formula property;

    @Inject
    private OctaveSimulationEngineFactory octaveSimulationEngineFactory;
    @Inject
    private STLVerifier verifier;

    public TrajectoryAnalysisComputation(Plotter plotter, Point point, OdeSystem odeSystem, Formula property, PrecisionConfiguration precision, OrthogonalSpace simulationSpace) {
        if (plotter == null) {
            throw new IllegalArgumentException("The parameter [plotter] is null.");
        }
        if (property == null) {
            throw new IllegalArgumentException("The parameter [property] is null.");
        }
        if (odeSystem == null) {
            throw new IllegalArgumentException("The parameter [odeSystem] is null.");
        }
        if (precision == null) {
            throw new IllegalArgumentException("The parameter [precision] is null.");
        }
        if (point == null) {
            throw new IllegalArgumentException("The parameter [point] is null.");
        }
        if (simulationSpace == null) {
            throw new IllegalArgumentException("The parameter [simulationSpace] is null.");
        }
        this.precision = precision;
        this.odeSystem = odeSystem;
        this.point = point;
        this.simulationSpace = simulationSpace;
        this.plotter = plotter;
        this.property = property;
    }

    @Override
    public Computation<Void> cloneComputation() {
        return new TrajectoryAnalysisComputation(plotter, point, odeSystem, property, precision, simulationSpace);
    }

    @Override
    public Void call() throws Exception {
        OctaveSimulationEngine simulationEgine = null;
        OctaveEngine script = null;
        try {
            LOGGER.info("analysis of " + point);
            // plot trajectory
            simulationEgine = octaveSimulationEngineFactory.simulationEngine(100000);
            Trajectory trajectory = simulationEgine.simulateAndPlot(point, odeSystem, simulationSpace.getMaxBounds().getTime(), precision);
            // plot robustness
            script = new OctaveEngineFactory().getScriptEngine();
            Monitor monitor = verifier.monitor(trajectory, property);
            ResultUtils.plotRecursively(monitor, script);
        } finally {
            plotter.addPlotterWindowListener(new CleanerListener(simulationEgine, script));
        }
        return Mergeable.Void.INSTANCE;
    }

    private static class CleanerListener implements PlotterWindowListener {

        private final OctaveSimulationEngine octaveSimulationEngine;
        private final OctaveEngine script;

        public CleanerListener(OctaveSimulationEngine octaveSimulationEngine, OctaveEngine script) {
            this.octaveSimulationEngine = octaveSimulationEngine;
            this.script = script;
        }

        @Override
        public void windowClosed(PlotterWindowEvent event) {
            if (octaveSimulationEngine != null) {
                octaveSimulationEngine.close();
            }
            if (script != null) {
                script.close();
            }
        }

    }

}
