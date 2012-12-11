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
package org.sybila.parasim.computation.density.distancecheck;

import org.sybila.parasim.computation.density.distancecheck.api.DistanceChecker;
import org.sybila.parasim.computation.density.distancecheck.cpu.OnePairDistanceChecker;
import org.sybila.parasim.computation.density.spawn.api.TrajectorySpawner;
import org.sybila.parasim.computation.density.spawn.cpu.FractionTrajectorySpawner;
import org.sybila.parasim.core.annotations.Provide;
import org.sybila.parasim.execution.api.annotations.ComputationScope;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@ComputationScope
public class ComputationDensityRegistrar {

    @Provide
    public TrajectorySpawner provideSpawner() {
        return new FractionTrajectorySpawner();
//        return new OneAndSurroundingsTrajectorySpawner();
    }

    @Provide
    public DistanceChecker provideDistanceChecker() {
        return new OnePairDistanceChecker();
    }

}
