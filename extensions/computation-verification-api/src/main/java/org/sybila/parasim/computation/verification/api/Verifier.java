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
package org.sybila.parasim.computation.verification.api;

import org.sybila.parasim.computation.cycledetection.api.CycleDetectedDataBlock;
import org.sybila.parasim.computation.cycledetection.api.CycleDetector;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.verification.Robustness;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Verifier<P extends Property> {

    <T extends Trajectory> VerifiedDataBlock<T> verify(DataBlock<T> trajectories, P property);

    <T extends Trajectory> VerifiedDataBlock<T> verify(CycleDetectedDataBlock<T> trajectories, P property);

    public Robustness verify(final Trajectory trajectory, final P property);

    public Robustness verify(final Trajectory trajectory, final P property, final CycleDetector detector);

    public Monitor monitor(final Trajectory trajectory, final P property);

    public Monitor monitor(final Trajectory trajectory, final P property, final CycleDetector detector);

}
