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

import org.sybila.parasim.computation.density.api.DistanceMetricDataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Robustness;

/**
 * Data block with information about trajectories robustness.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface VerifiedDataBlock<T extends Trajectory> extends DistanceMetricDataBlock<T> {

    /**
     * Returns robustness of specified trajectory.
     * @param index Index of a trajectory.
     * @return Robustness of trajectory with given index.
     */
    public Robustness getRobustness(int index);
}
