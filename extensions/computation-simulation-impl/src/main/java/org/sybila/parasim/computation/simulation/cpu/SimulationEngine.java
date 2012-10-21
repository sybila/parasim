package org.sybila.parasim.computation.simulation.cpu;

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface SimulationEngine {

    void close();

    Trajectory simulate(Point point, OdeSystem odeSystem, double timeStep, double timeLimit);

}
