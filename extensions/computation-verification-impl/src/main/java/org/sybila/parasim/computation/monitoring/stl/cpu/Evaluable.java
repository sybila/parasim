package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.model.verification.stl.TimeInterval;
import org.sybila.parasim.computation.monitoring.api.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.List;

/**
 * Enables the evaluation of a property's robustness on a given trajectory.
 * For computational purposes the trajectory may be looked at as prolonged past
 * it's last point using the last point's value and valueDerivative.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @param <T> Class of trajectories on which property robustness computation
 *            is enabled.
 * @param <R> Class of property robustness that will be computed.
 */
public interface Evaluable<T extends Trajectory, R extends PropertyRobustness> {

    /**
     * Evaluates the property's robustness on the given trajectory over a segment
     * covering the given time interval.
     *
     * If the trajectory is null or empty an exception is thrown.
     *
     * If the trajectory is shorter then the required interval it is prolonged
     * using it's last point and it's value's derivative.
     *
     * @param trajectory Trajectory on which to evaluate robustness.
     * @param interval Time interval over which to evaluate robustness.
     * @return List of time points with computed robustness values and their
     *         derivatives. The time of the first point will be
     *         precisely <code>interval.getLowerBound()</code>, the
     *         time of the last point will be <code>interval.getUpperBound()</code>
     *         or less depending on the length of the trajectory.
     */
    List<R> evaluate(T trajectory, TimeInterval interval);
}
