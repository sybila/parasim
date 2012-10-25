package org.sybila.parasim.computation.simulation.octave;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;
import java.util.Arrays;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngineFactory;
import org.sybila.parasim.model.ode.OctaveOdeSystem;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public enum OdePkgEngineFactory implements SimulationEngineFactory {

    ODE5R("ode5r"),
    ODE78("ode78"),
    ODEBDA("odebda"),
    ODEBDI("odebdi"),
    ODEKDI("odekdi"),
    ODERS("oders"),
    ODESX("odesx");


    private final String function;

    private OdePkgEngineFactory(String function) {
        this.function = function;
    }

    @Override
    public boolean isAvailable() {
        try {
            new OctaveEngineFactory().getScriptEngine().eval("pkg load odepkg;");
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public SimulationEngine simulationEngine(long stepLimit, double relativeTolerance) {
        return new OdePkgEngine(function, new OctaveEngineFactory().getScriptEngine(), stepLimit, relativeTolerance);
    }


    private final class OdePkgEngine extends OctaveSimulationEngine {

        private final String function;

        public OdePkgEngine(String function, OctaveEngine octave, long stepLimit, double relativeTolerance) {
            super(octave, stepLimit, relativeTolerance);
            this.function = function;
        }

        @Override
        protected OctaveDouble rawSimulation(Point point, OctaveOdeSystem odeSystem, long numberOfIterations, PrecisionConfiguration precision) {
            getOctave().eval(odeSystem.octaveString(true));
            getOctave().eval("pkg load odepkg;");
            getOctave().eval("vopt = odeset('RelTol', " + getRelativeTolerance() + ", 'AbsTol', " + Long.MAX_VALUE + ", 'InitialStep', " + precision.getTimeStep() + ", 'MaxStep', " + precision.getTimeStep() + ");");
            getOctave().eval("y = " + function + "(@f, [" + point.getTime() + ", " + (numberOfIterations * precision.getTimeStep()) + "], " + Arrays.toString(point.toArray(odeSystem.getVariables().size())) + ", vopt);");
            getOctave().eval("result = getfield(y, 'y');");
            return getOctave().get(OctaveDouble.class, "result");
        }

    }
}
