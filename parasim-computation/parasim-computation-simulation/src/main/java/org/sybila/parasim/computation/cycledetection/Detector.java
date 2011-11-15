package org.sybila.parasim.computation.cycledetection;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Enables cycle detection over data blocks of trajectories.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
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
     * Given a set of trajectories and an array of CycleDetectors
     * each trajectory is subjected to cycle detection while a limit on the number
     * of computation steps to carry out on each trajectory is given.
     *
     * If stepLimit = 0 each computation is carried out until a cycle is found or
     * the end of the trajectory is reached.
     *     
     * @param trajectories DataBlock of trajectories.
     * @param detectors Array of cycle detectors used previously to detect cycles
     *        on the given trajectories.
     * @param stepLimit Limit on amount of work to do on each trajectory.
     * @return Cycle detection data block with results.
     */
    Out detect(DataBlock<Trajectory> trajectories, CD[] detectors, int stepLimit);

    /**
     * Given a set of old trajectories and old CycleDetectors and a
     * CycleDetectorFactory to build new CycleDetectors and a set of new trajectories
     * each trajectory is subjected to cycle detection while a limit on the number
     * of computation steps to carry out on each trajectory is given.
     *
     * If stepLimit = 0 each computation is carried out until a cycle is found or
     * the end of the trajectory is reached.
     *
     * The results are returned in a combined structure with oldTrajectories
     * at the begining and newTrajectories after them.
     *
     * @param factory CycleDetectorFactory to build new cycle detectors.
     * @param oldTrajectories DataBlock of trajectories that have already been
     *        partialy processed in previous iteration.
     * @param oldDetectors Array of cycle detectors used previously to detect cycles
     *        on oldTrajectories.
     * @param newTrajectories DataBlock of trajectories that have not yet had any
     *        cycle detection performed on them.
     * @param stepLimit Limit on amount of work to do on each trajectory.
     * @return Cycle detection data block with results.
     */
    Out detect(CDF factory, DataBlock<Trajectory> oldTrajectories, CD[] oldDetectors, DataBlock<Trajectory> newTrajectories, int stepLimit);
}
