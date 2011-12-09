package org.sybila.parasim.computation.monitoring.stl.cpu;

import org.sybila.parasim.computation.monitoring.PropertyRobustness;
import org.sybila.parasim.model.trajectory.Trajectory;
import java.util.List;

/**
 * Enables the evaluation of a property's robustness on a given trajectory.
 * For computational purposes the trajectory may be prolonged past it's last
 * point usint the last points value and valueDerivative.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 * @param <T> Class of trajectories on which property robustness computation
 *            is enabled.
 * @param <R> Class of property robustness that will be computed.
 */
public interface Evaluable<T extends Trajectory, R extends PropertyRobustness>
{
    /**
     * Evaluates the property's robustness on the given trajectory over a segment
     * covering a time interval starting at <b>a</b> time units from the begining of
     * the trajectory and ending in <b>b</b> time units from the begining.
     *
     * If the trajectory is null or empty an exception is thrown.
     *
     * If the trajectory is shorter then the required interval it is prolonged
     * using it's last point and it's value's derivative.
     *
     * @param trajectory Trajectory on which to evaluate robustness.
     * @param a Begining of time interval over which to evaluate robustness.
     * @param b End of time interval over which to evaluate robustness.
     * @return List of time points with computed robustness values and their
     *         derivatives. The time of the first point will be <b>a</b>, the
     *         time of the last point will be <b>b</b> or less depending on the
     *         the length of the trajectory.
     */
    List<R> evaluate(T trajectory, float a, float b);
}
