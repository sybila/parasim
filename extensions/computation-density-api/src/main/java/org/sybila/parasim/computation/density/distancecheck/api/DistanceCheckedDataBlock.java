package org.sybila.parasim.computation.density.distancecheck.api;

import org.sybila.parasim.computation.density.api.LimitedDistance;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface DistanceCheckedDataBlock extends DataBlock<Trajectory> {

    LimitedDistance getDistance(int index, int neighborIndex);

    int getNeighborCheckedPosition(int index, int neighborIndex);

    int getTrajectoryCheckedPosition(int index, int neighborIndex);
}
