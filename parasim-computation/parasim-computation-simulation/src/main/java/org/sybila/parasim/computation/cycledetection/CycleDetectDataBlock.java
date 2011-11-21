package org.sybila.parasim.computation.cycledetection;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Represents intermediate results of cycle detection over serveral trajectories.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface CycleDetectDataBlock<T extends Trajectory, CD extends CycleDetector> extends DataBlock<T>
{

    /**
     * Returns the status of cycle detection for the given trajectory.
     *
     * @param index number from interval [0, number of trajectories)
     * @return trajectory status
     */
    CycleDetectionStatus getStatus(int index);

    /**
     * Sets the cycle detection status of trajectory with given index.
     * 
     * @param index Index of trajectory who's status to modify.
     * @param status New status of given trajectory.
     */
    void setStatus(int index, CycleDetectionStatus status);


    /**
     * Returns the cycle detector used to detect cycles on trajectory with given
     * index.
     * 
     * @param index Index of the detector.
     * @return Cycle detector used to detect a cycle on trajectory with given index.
     */
    CD getCycleDetector(int index);
}
