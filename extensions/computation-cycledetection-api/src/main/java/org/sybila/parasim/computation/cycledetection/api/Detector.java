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
package org.sybila.parasim.computation.cycledetection.api;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Enables cycle detection over data blocks of trajectories.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dra�an</a>
 */
public interface Detector<CD extends CycleDetector, CDF extends CycleDetectorFactory<CD>, Out extends CycleDetectDataBlock> {

    /**
     * Given a CycleDetectorFactory to build CycleDetectors and a set of trajectories
     * each trajectory is subjected to cycle detection while a limit on the number
     * of computation steps to carry out on each trajectory is given.
     *
     * @param factory CycleDetectorFactory to build new cycle detectors.
     * @param trajectories DataBlock of trajectories.
     * @param stepLimit Limit on amount of work to do on each trajectory.
     * @return Cycle detection data block with results.
     */
    Out detect(CDF factory, DataBlock<Trajectory> trajectories, int stepLimit);

    /**
     * Given a set of trajectories that have already been subject to cycle
     * detection the computation is resumed with a limit on the number
     * of computation steps to carry out on each trajectory.
     *
     * If stepLimit = 0 each computation is carried out until a cycle is found or
     * the end of the trajectory is reached.
     *
     * @param trajectories Cycle detection data block of trajectories.
     * @param stepLimit Limit on amount of work to do on each trajectory.
     * @return Cycle detection data block with results.
     */
    Out detect(Out trajectories, int stepLimit);

    /**
     * Given a set of old trajectories that have alreadz been subject to cycle
     * detection and a new trajectories and a CycleDetectorFactory to build new
     * CycleDetectors each trajectory is subjected to cycle detection while a
     * limit on the number of computation steps to carry out on each trajectory is given.
     *
     * If stepLimit = 0 each computation is carried out until a cycle is found or
     * the end of the trajectory is reached.
     *
     * The results are returned in one datablock with oldTrajectories
     * at the begining and newTrajectories after them.
     *
     * @param factory CycleDetectorFactory to build new cycle detectors.
     * @param oldTrajectories DataBlock of trajectories that have already been
     *        partialy processed in previous iteration.
     * @param newTrajectories DataBlock of trajectories that have not yet had any
     *        cycle detection performed on them.
     * @param stepLimit Limit on amount of work to do on each trajectory.
     * @return Cycle detection data block with results.
     */
    //Out detect(CDF factory, Out oldTrajectories, DataBlock<Trajectory> newTrajectories, int stepLimit);
}
