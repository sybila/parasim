package org.sybila.parasim.model.verification.result;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Data block with information about trajectories robustness.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface VerifiedDataBlock<T extends Trajectory> extends DataBlock<T> {

    /**
     * Returns robustness of specified trajectory.
     * @param index Index of a trajectory.
     * @return Robustness of trajectory with given index.
     */
    public float getRobustness(int index); //TODO něco jiného než float
}
