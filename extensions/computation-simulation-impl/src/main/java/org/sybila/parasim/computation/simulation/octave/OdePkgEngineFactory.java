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
package org.sybila.parasim.computation.simulation.octave;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;
import java.util.Arrays;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.model.ode.OctaveOdeSystem;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public enum OdePkgEngineFactory implements OctaveSimulationEngineFactory {

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
    public OctaveSimulationEngine simulationEngine(long stepLimit) {
        return new OdePkgEngine(function, new OctaveEngineFactory().getScriptEngine(), stepLimit);
    }


    private final class OdePkgEngine extends OctaveSimulationEngine {

        private final String function;

        public OdePkgEngine(String function, OctaveEngine octave, long stepLimit) {
            super(octave, stepLimit);
            this.function = function;
        }

        @Override
        protected OctaveDouble rawSimulation(Point point, OctaveOdeSystem odeSystem, long numberOfIterations, PrecisionConfiguration precision) {
            getOctave().eval(odeSystem.octaveString(true));
            getOctave().eval("pkg load odepkg;");
            getOctave().eval("vopt = odeset('RelTol', " + precision.getMaxRelativeError() + ", 'AbsTol', " + Long.MAX_VALUE + ", 'InitialStep', " + precision.getTimeStep() + ", 'MaxStep', " + precision.getTimeStep() + ");");
            getOctave().eval("y = " + function + "(@f, [" + point.getTime() + ", " + (numberOfIterations * precision.getTimeStep()) + "], " + Arrays.toString(point.toArray(odeSystem.getVariables().size())) + ", vopt);");
            getOctave().eval("result = getfield(y, 'y');");
            return getOctave().get(OctaveDouble.class, "result");
        }

    }
}
