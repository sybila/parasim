/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
public class LsodeEngineFactory implements OctaveSimulationEngineFactory {

    private final IntegrationMethod integrationMethod;

    public LsodeEngineFactory(IntegrationMethod integrationMethod) {
        this.integrationMethod = integrationMethod;
    }

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
    public OctaveSimulationEngine simulationEngine(long stepLimit) {
        return new LsodeEngine(new OctaveEngineFactory().getScriptEngine(), integrationMethod, stepLimit);
    }

    private static class LsodeEngine extends OctaveSimulationEngine {

        public LsodeEngine(OctaveEngine octave, IntegrationMethod integrationMethod, long stepLimit) {
            super(octave, stepLimit);
            StringBuilder builder = new StringBuilder()
                    .append("lsode_options(\"step limit\", ")
                    .append(Long.toString(stepLimit))
                    .append(");")
                    .append("lsode_options('integration method', '")
                    .append(integrationMethod.getName())
                    .append("');");
            LOGGER.debug(builder.toString());
            getOctave().eval(builder.toString());
        }

        @Override
        protected OctaveDouble rawSimulation(Point point, OctaveOdeSystem odeSystem, long numberOfIterations, PrecisionConfiguration precision) {
            StringBuilder builder = new StringBuilder();
            if (precision.getMaxRelativeError() > 0) {
                builder.append("lsode_options(\"relative tolerance\", ").append(precision.getMaxRelativeError()).append(");");
            }
            if (absoluteToleranceIsSet(precision)) {
                float[] tolerance = new float[precision.getDimension()];
                for (int i=0; i<tolerance.length; i++) {
                    tolerance[i] = precision.getMaxAbsoluteError(i);
                }
                builder.append("lsode_options(\"absolute tolerance\", ").append(Arrays.toString(tolerance)).append(");");
            }
            builder.append(odeSystem.octaveString(false));
            builder.append("i = ").append(Arrays.toString(point.toArray(odeSystem.getVariables().size()))).append(";");
            builder.append("t = linspace(").append(point.getTime()).append(", ").append(numberOfIterations * precision.getTimeStep()).append(", ").append(numberOfIterations).append(");");
            builder.append("y = lsode(\"").append(odeSystem.octaveName()).append("\", i, t);");
            LOGGER.debug(builder.toString());
            getOctave().eval(builder.toString());
            return getOctave().get(OctaveDouble.class, "y");

        }
    }

    public static enum IntegrationMethod {

        ADAMS("adams"),
        NONSTIFF("non-stiff"),
        BDF("bdf"),
        STIFF("stiff");

        private final String name;

        private IntegrationMethod(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
