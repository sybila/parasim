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
package org.sybila.parasim.computation.verification.cpu;

import org.sybila.parasim.computation.verification.api.MonitorFactory;
import java.util.Iterator;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.cycledetection.api.CycleDetectedDataBlock;
import org.sybila.parasim.computation.cycledetection.api.CycleDetector;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.computation.verification.api.VerifiedDataBlock;
import org.sybila.parasim.computation.verification.api.Verifier;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.LimitedPointDistanceMetric;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleVerifier<P extends Property> implements Verifier<P> {

    private final MonitorFactory<P> monitorFactory;

    public SimpleVerifier(MonitorFactory<P> monitorFactory) {
        Validate.notNull(monitorFactory);
        this.monitorFactory = monitorFactory;
    }

    @Override
    public <T extends Trajectory> VerifiedDataBlock<T> verify(final DataBlock<T> trajectories, P property) {
        final Robustness[] robustnesses = new Robustness[trajectories.size()];
        int counter = 0;
        for (Trajectory trajectory: trajectories) {
            robustnesses[counter] = verify(trajectory, property);
            counter++;
        }
        return new VerifiedDataBlock<T>() {

            @Override
            public LimitedPointDistanceMetric getDistanceMetric(int index) {
                return robustnesses[index];
            }

            @Override
            public Robustness getRobustness(int index) {
                return robustnesses[index];
            }

            @Override
            public T getTrajectory(int index) {
                return (T) trajectories.getTrajectory(index).getReference().getTrajectory();
            }

            @Override
            public int size() {
                return trajectories.size();
            }

            @Override
            public Iterator<T> iterator() {
                return trajectories.iterator();
            }
        };
    }

    @Override
    public <T extends Trajectory> VerifiedDataBlock<T> verify(final CycleDetectedDataBlock<T> trajectories, P property) {
        final Robustness[] robustnesses = new Robustness[trajectories.size()];
        int counter = 0;
        for (Trajectory trajectory: trajectories) {
            CycleDetector detector = trajectories.getCycleDetector(counter);
            robustnesses[counter] = verify(trajectory, property, detector);
            counter++;
        }
        return new VerifiedDataBlock<T>() {

            @Override
            public LimitedPointDistanceMetric getDistanceMetric(int index) {
                return robustnesses[index];
            }

            @Override
            public Robustness getRobustness(int index) {
                return robustnesses[index];
            }

            @Override
            public T getTrajectory(int index) {
                return (T) trajectories.getTrajectory(index).getReference().getTrajectory();
            }

            @Override
            public int size() {
                return trajectories.size();
            }

            @Override
            public Iterator<T> iterator() {
                return trajectories.iterator();
            }
        };
    }

    @Override
    public Robustness verify(final Trajectory trajectory, final P property) {
        return verify(trajectory, property, CycleDetector.CYCLE_IS_NOT_DETECTED);
    }

    @Override
    public Robustness verify(final Trajectory trajectory, final P property, final CycleDetector detector) {
        return monitor(trajectory, property, detector).getRobustness(0);
    }

    @Override
    public Monitor monitor(final Trajectory trajectory, final P property) {
        return monitor(trajectory, property, CycleDetector.CYCLE_IS_NOT_DETECTED);
    }

    @Override
    public Monitor monitor(final Trajectory trajectory, final P property, final CycleDetector detector) {
        Trajectory toVerify = detector.isCycleDetected() ? new ExpandedTrajectory(trajectory, detector, property) : trajectory;
        return monitorFactory.createMonitor(toVerify, property);
    }

}
