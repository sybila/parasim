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
package org.sybila.parasim.computation.simulation.cpu;

import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.api.AdaptiveStepSimulator;
import org.sybila.parasim.computation.simulation.api.ArraySimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.api.Status;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.ListDataBlock;
import org.sybila.parasim.model.trajectory.ListTrajectory;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@Deprecated
public class Rkf45Simulator implements AdaptiveStepSimulator {
    /**
     * Minimal time step using during simulation
     */
    public static final float MINIMAL_TIME_STEP = (float) 0.00000000001;

    private static final float A2 = (float) (1.0 / 4.0);
    private static final float B2 = (float) (1.0 / 4.0);
    private static final float A3 = (float) (3.0 / 8.0);
    private static final float B3 = (float) (3.0 / 32.0);
    private static final float C3 = (float) (9.0 / 32.0);
    private static final float A4 = (float) (12.0 / 13.0);
    private static final float B4 = (float) (1932.0 / 2197.0);
    private static final float C4 = (float) (-7200.0 / 2197.0);
    private static final float D4 = (float) (7296.0 / 2197.0);
    private static final float A5 = (float) 1.0;
    private static final float B5 = (float) (439.0 / 216.0);
    private static final float C5 = (float) (-8.0);
    private static final float D5 = (float) (3680.0 / 513.0);
    private static final float E5 = (float) (-845.0 / 4104.0);
    private static final float A6 = (float) (1.0 / 2.0);
    private static final float B6 = (float) (-8.0 / 27.0);
    private static final float C6 = (float) 2.0;
    private static final float D6 = (float) (-3544.0 / 2565.0);
    private static final float E6 = (float) (1859.0 / 4104.0);
    private static final float F6 = (float) (-11.0 / 40.0);
    private static final float R1 = (float) (1.0 / 360.0);
    private static final float R3 = (float) (-128.0 / 4275.0);
    private static final float R4 = (float) (-2197.0 / 75240.0);
    private static final float R5 = (float) (1.0 / 50.0);
    private static final float R6 = (float) (2.0 / 55.0);
    private static final float N1 = (float) (25.0 / 216.0);
    private static final float N3 = (float) (1408.0 / 2565.0);
    private static final float N4 = (float) (2197.0 / 4104.0);
    private static final float N5 = (float) (-1.0 / 5.0);

    /**
     * It executes Runge-Kutta-Fehlberg 45 simulation algorithm to generate trajectories.
     *
     * @param configuration configuration of the simulation
     * @param data trajectories whose last points are used as init point of simulations
     * @return simulated data
     */
    @Override
    public <T extends Trajectory> SimulatedDataBlock<T> simulate(final AdaptiveStepConfiguration configuration, DataBlock<T> data) {
        List<T> trajectories = new ArrayList<>(data.size());
        Status[] statuses = new Status[data.size()];
        for (int i = 0; i < data.size(); i++) {
            Rkf45Computation computation = new Rkf45Computation(configuration);
            trajectories.add((T) simulate(computation, data.getTrajectory(i).getLastPoint()));
            statuses[i] = computation.status;
        }
        return new ArraySimulatedDataBlock<>(new ListDataBlock<>(trajectories), statuses);
    }

    private void prepareCoefficents(Rkf45Computation computation, float[] pointData) {
        float[] data = new float[pointData.length];
        System.arraycopy(pointData, 0, data, 0, pointData.length);
        // K1
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            computation.k1[dim] = computation.timeStep * computation.configuration.getOdeSystem().getVariable(dim).evaluate(data);
        }
        // K2
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            data[dim] = data[dim] + B2 * computation.k1[dim];
        }
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            computation.k2[dim] = computation.timeStep * computation.configuration.getOdeSystem().getVariable(dim).evaluate(data);
        }
        System.arraycopy(pointData, 0, data, 0, pointData.length);
        // K3
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            data[dim] = data[dim] + B3 * computation.k1[dim] + C3 * computation.k2[dim];
        }
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            computation.k3[dim] = computation.timeStep * computation.configuration.getOdeSystem().getVariable(dim).evaluate(data);
        }
        System.arraycopy(pointData, 0, data, 0, pointData.length);
        // K4
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            data[dim] = data[dim] + B4 * computation.k1[dim] + C4 * computation.k2[dim] + D4 * computation.k3[dim];
        }
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            computation.k4[dim] = computation.timeStep * computation.configuration.getOdeSystem().getVariable(dim).evaluate(data);
        }
        System.arraycopy(pointData, 0, data, 0, pointData.length);
        // K5
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            data[dim] = data[dim] + B5 * computation.k1[dim] + C5 * computation.k2[dim] + D5 * computation.k3[dim] + E5 * computation.k4[dim];
        }
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            computation.k5[dim] = computation.timeStep * computation.configuration.getOdeSystem().getVariable(dim).evaluate(data);
        }
        System.arraycopy(pointData, 0, data, 0, pointData.length);
        // K6
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            data[dim] = data[dim] + B6 * computation.k1[dim] + C6 * computation.k2[dim] + D6 * computation.k3[dim] + E6 * computation.k4[dim] + F6 * computation.k5[dim];
        }
        for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
            computation.k6[dim] = computation.timeStep * computation.configuration.getOdeSystem().getVariable(dim).evaluate(data);
        }
    }

    private Point successor(Rkf45Computation computation, final Point point) {
        float[] successorData = new float[point.getDimension()];
        float[] auxiliaryData = new float[point.getDimension()];
        float[] absoluteError = new float[point.getDimension()];
        float[] previousData = point.toArray();
        simulation: for (; computation.iteration < computation.configuration.getMaxNumberOfIterations(); computation.iteration++) {
            prepareCoefficents(computation, previousData);
            // Absolute error
            for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
                absoluteError[dim] = Math.abs(R1 * computation.k1[dim] + R3 * computation.k3[dim] + R4 * computation.k4[dim] + R5 * computation.k5[dim] + R6 * computation.k6[dim]);
                if (computation.configuration.getPrecisionConfiguration().getMaxAbsoluteError(dim) != 0 && absoluteError[dim] > computation.configuration.getPrecisionConfiguration().getMaxAbsoluteError(dim)) {
                    computation.timeStep /= 2;
                    if (computation.timeStep < MINIMAL_TIME_STEP) {
                        computation.status = Status.PRECISION;
                        return null;
                    }
                    continue simulation;
                }
            }
            // Successor data
            for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
                successorData[dim] = previousData[dim] + N1 * computation.k1[dim] + N3 * computation.k3[dim] + N4 * computation.k4[dim] + N5 * computation.k5[dim];
            }
            // Relative error
            for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
                if (computation.configuration.getPrecisionConfiguration().getMaxRelativeError() != 0 && Math.abs(absoluteError[dim] / successorData[dim]) > computation.configuration.getPrecisionConfiguration().getMaxRelativeError()) {
                    computation.timeStep /= 2;
                    if (computation.timeStep < MINIMAL_TIME_STEP) {
                        computation.status = Status.PRECISION;
                        return null;
                    }
                    continue simulation;
                }
            }
            // Check the distance
            for (int dim = 0; dim < computation.configuration.getDimension(); dim++) {
                if (Math.abs(successorData[dim] - previousData[dim]) > computation.configuration.getSteps()[dim]) {
                    computation.timeStep /= 2;
                    if (computation.timeStep < MINIMAL_TIME_STEP) {
                        computation.status = Status.PRECISION;
                        return null;
                    }
                    continue simulation;
                }
            }
            // Check the min and max bounds
            if (!computation.configuration.getSpace().isIn(successorData)) {
                computation.status = Status.BOUNDS;
                return null;
            }
            return new ArrayPoint(point.getTime() + computation.timeStep, successorData);
        }
        return null;
    }

    private Trajectory simulate(Rkf45Computation computation, Point initial) {
        Point current = initial;
        List<Point> points = new ArrayList<>();
        float timeStep = computation.timeStep;
        float savedTimeStep = 0;
        float  timeStepCorrection = 0;
        float lastTime = initial.getTime();
        for (computation.iteration = 0; computation.iteration < computation.configuration.getMaxNumberOfIterations(); computation.iteration++) {
            current = successor(computation, current);
            if (current == null) {
                break;
            }
            if (current.getTime() > computation.configuration.getSpace().getMaxBounds().getTime()) {
                computation.status = Status.OK;
                break;
            }
            if (current.getTime() < lastTime + timeStep) {
                timeStepCorrection = lastTime + timeStep - current.getTime();
                savedTimeStep = computation.timeStep;
                computation.timeStep = timeStepCorrection;
            } else {
                if (savedTimeStep != 0) {
                    computation.timeStep = (timeStepCorrection == computation.timeStep ? savedTimeStep : computation.timeStep);
                    savedTimeStep = 0;
                    lastTime += timeStep;
                }
                points.add(current);
            }
        }
        if (computation.status == null) {
            computation.status = Status.TIMEOUT;
        }
        return new ListTrajectory(points);
    }

    /**
     * Auxiliary class for computation of RKF 45 method
     */
    private class Rkf45Computation {

        /**
         * Simulation configuration
         */
        public AdaptiveStepConfiguration configuration;
        /**
         * Iteration which is currently processed
         */
        public int iteration;
        /**
         * Status which will be returned for currently processed trajectory
         */
        public Status status;
        /**
         * Time step between two subsequent points
         */
        public float timeStep;
        // RKF 45 coefficients
        public float[] k1;
        public float[] k2;
        public float[] k3;
        public float[] k4;
        public float[] k5;
        public float[] k6;

        public Rkf45Computation(AdaptiveStepConfiguration configuration) {
            this.configuration = configuration;
            timeStep = configuration.getPrecisionConfiguration().getTimeStep();
            k1 = new float[configuration.getDimension()];
            k2 = new float[configuration.getDimension()];
            k3 = new float[configuration.getDimension()];
            k4 = new float[configuration.getDimension()];
            k5 = new float[configuration.getDimension()];
            k6 = new float[configuration.getDimension()];

        }
    }
}
