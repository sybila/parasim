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

import java.util.Iterator;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.computation.verification.api.Verifier;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;
import org.sybila.parasim.model.verification.result.VerifiedDataBlock;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleVerifier<P extends Property> implements Verifier<P> {

    private final MonitorFactory<P> monitorFactory;

    public SimpleVerifier(MonitorFactory<P> monitorFactory) {
        Validate.notNull(monitorFactory);
        this.monitorFactory = monitorFactory;
    }

    public VerifiedDataBlock<Trajectory> verify(final DataBlock<Trajectory> trajectories, P property) {
        final Robustness[] robustnesses = new Robustness[trajectories.size()];
        int counter = 0;
        for (Trajectory trajectory: trajectories) {
            robustnesses[counter] = monitorFactory.createMonitor(trajectory, property).getRobustness(0);
            counter++;
        }
        return new VerifiedDataBlock<Trajectory>() {

            public Robustness getRobustness(int index) {
                return robustnesses[index];
            }

            public Trajectory getTrajectory(int index) {
                return trajectories.getTrajectory(index);
            }

            public int size() {
                return trajectories.size();
            }

            public Iterator<Trajectory> iterator() {
                return trajectories.iterator();
            }
        };
    }
}
