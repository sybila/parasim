package org.sybila.parasim.computation.density.distancecheck.api;

import org.sybila.parasim.computation.density.api.Configuration;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface DistanceChecker {

    /**
     * Checks distances between trajectories in datablock and their neighborhood
     * defined in configuration
     *
     * @param congfiguration
     * @param trajectories
     * @return
     */
    DistanceCheckedDataBlock check(Configuration congfiguration, DataBlock<Trajectory> trajectories);
}
