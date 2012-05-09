package org.sybila.parasim.computation.density.api;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.LimitedPointDistanceMetric;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface DistanceMetricDataBlock<T extends Trajectory> extends DataBlock<T> {

    LimitedPointDistanceMetric getDistanceMetric(int index);

}
