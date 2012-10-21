package org.sybila.parasim.computation.simulation.octave;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;
import java.util.Arrays;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngineFactory;
import org.sybila.parasim.model.ode.OctaveOdeSystem;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class LsodeEngineFactory implements SimulationEngineFactory {

    @Override
    public boolean isAvailable() {
        try {
            new OctaveEngineFactory().getScriptEngine();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public SimulationEngine simulationEngine(long stepLimit, double relativeTolerance) {
        return new LsodeEngine(new OctaveEngineFactory().getScriptEngine(), stepLimit, relativeTolerance);
    }

    private static class LsodeEngine extends OctaveSimulationEngine {

        public LsodeEngine(OctaveEngine octave, long stepLimit, double relativeTolerance) {
            super(octave, stepLimit, relativeTolerance);
            getOctave().eval("lsode_options(\"step limit\", " + stepLimit + ");");
            if (relativeTolerance > 0) {
                getOctave().eval("lsode_options(\"relative tolerance\", " + relativeTolerance + ");");
            }
        }

        @Override
        protected OctaveDouble rawSimulation(Point point, OctaveOdeSystem odeSystem, long numberOfIterations, double timeStep) {
            getOctave().eval(odeSystem.octaveString());
            getOctave().eval("i = " + Arrays.toString(point.toArray(odeSystem.dimension())) + ";");
            getOctave().eval("t = linspace(" + point.getTime() + ", " + numberOfIterations * timeStep + ", " + numberOfIterations + ");");
            getOctave().eval("y = lsode(\"" + odeSystem.octaveName() + "\", i, t);");
            return getOctave().get(OctaveDouble.class, "y");

        }
    }
}
