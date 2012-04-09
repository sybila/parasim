package org.sybila.parasim.computation.monitoring.api;

import org.sybila.parasim.model.distance.Distance;

/**
 * Represents the robustness of a property on a single trajectory of an OdeSystem.
 *
 * It is a number in some time moment stating how much another trajectory's
 * secondary signal may deviate from this one's and still have the same property
 * validity.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PropertyRobustness extends Distance {
    /**
     * Returns the time for which the robustness was computed.
     * @return time
     */
    float getTime();

    /**
     * Returns the derivative of the value in given point. This is due to the
     * fact that a trajectory's robustness may not be continuous from the left
     * and thus to compute a value in between two neighbouring time points
     * the value's derivatives are necessary.
     *
     * @return derivate of value in the time where it was computed
     */
    float getValueDerivative();
}
