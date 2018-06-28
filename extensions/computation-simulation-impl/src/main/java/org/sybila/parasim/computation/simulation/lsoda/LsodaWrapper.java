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
package org.sybila.parasim.computation.simulation.lsoda;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.simulation.SimulatorRegistrar;
import org.sybila.parasim.model.math.*;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.ode.LsodaOdeSystem;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;

/**
 * @author <a href="mailto:pejznoch@gmail.com">Ales Pejznoch</a>
 */
public class LsodaWrapper {

    float[] rawSimulation(Point point, LsodaOdeSystem odeSystem,
                          long numberOfIterations, PrecisionConfiguration precision)
            throws Exception {

        float startTime = point.getTime();
        float timeStep = precision.getTimeStep();
        float maxRelativeError = precision.getMaxRelativeError();

        float[] maxAbsoluteErrors = new float[odeSystem.dimension()];
        for (int i=0; i<odeSystem.dimension(); i++) {
            maxAbsoluteErrors[i] = precision.getMaxAbsoluteError(i);
        }

        float[] initialValues = point.toArray(odeSystem.dimension());

        byte[][] tree = new byte[odeSystem.dimension()][];
        float[][] constants = new float[odeSystem.dimension()][];
        int[][] variables = new int[odeSystem.dimension()][];



        Expression.position p = new Expression.position();

        for (int i=0; i<odeSystem.dimension(); ++i) {
            p.index = 0;
            tree[i] = new byte[odeSystem.getVariable(i).getNodeCount()];
            constants[i] = new float[odeSystem.getVariable(i).getNodeCount()];
            variables[i] = new int[odeSystem.getVariable(i).getNodeCount()];

            odeSystem.getVariable(i).getRightSideExpression().serialize(tree[i], constants[i], variables[i], p);
        }

        LsodaResult result = wrappedFunc(tree,
                                         constants,
                                         variables,
                                         initialValues,
                                         startTime,
                                         timeStep,
                                         numberOfIterations,
                                         maxRelativeError,
                                         maxAbsoluteErrors);

        if (result.failed==1) {
            LoggerFactory.getLogger(SimulatorRegistrar.class).error("Lsoda: " + result.errorMsg);
            throw new Exception("Exception from Lsoda solver");
        }

        return result.output;
    }

//    static{ System.load("/home/ales/pr/parasim/parasim/lsodacpp/build/libs/sim/shared/libsim.so"); }
    static{
        System.loadLibrary("sim");
    }

    private native LsodaResult wrappedFunc(byte[][] tree,
                                        float[][] constants,
                                        int[][] variables,
                                        float[] initialValues,
                                        float startTime,
                                        float timeStep,
                                        long maxSteps,
                                        float maxRelativeError,
                                        float[] maxAbsoluteErrors);
}