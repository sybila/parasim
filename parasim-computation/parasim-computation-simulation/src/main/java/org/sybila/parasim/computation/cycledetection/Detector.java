package org.sybila.parasim.computation.cycledetection;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.trajectory.PointComparator;

/**
 * Enables cycle detection over data blocks of trajectories.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface Detector<Comparator extends PointComparator, CD extends CycleDetector, Out extends CycleDetectDataBlock> {

    /**
     * Given a comparator to compare two points and a set of trajectories
     * each trajectory is subjected to cycle detection while a limit on the number
     * of computation steps to carry out on each trajectory is given.
     *
     * @param comparator Comparator to compare points on trajectory.
     * @param trajectories DataBlock of trajectories.
     * @param stepLimit Limit on amount of work to do on each trajectory.
     * @return Cycle detection data block with results.
     */
    Out detect(Comparator comparator, DataBlock<Trajectory> trajectories, int stepLimit);

    /**
     * Given a comparator to compare two points and a set of trajectories
     * each trajectory is subjected to cycle detection while a limit on the number
     * of computation steps to carry out on each trajectory is given.
     *
     * If stepLimit = 0 each computation is carried out until a cycle is found or
     * the end of the trajectory is reached.
     *
     * @param comparator Comparator to compare points on trajectory.
     * @param trajectories DataBlock of trajectories.
     * @param detectors Array of cycle detectors used previously to detect cycles
     *        on the given trajectories.
     * @param stepLimit Limit on amount of work to do on each trajectory.
     * @return Cycle detection data block with results.
     */
    Out detect(Comparator comparator, DataBlock<Trajectory> trajectories, CD[] detectors, int stepLimit);
}
