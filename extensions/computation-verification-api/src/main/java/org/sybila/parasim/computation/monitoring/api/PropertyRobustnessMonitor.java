package org.sybila.parasim.computation.monitoring.api;

import org.sybila.parasim.model.verification.Property;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Represents a method to compute the robustness of a property on a trajectory
 * of an OdeSystem.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PropertyRobustnessMonitor<P extends Property, T extends Trajectory, R extends PropertyRobustness> {
    /**
     * Computes the robustness of the property on the trajectory.
     *
     * @param property Property who's robustness to compute on trajectory.
     * @param trajectory Trajectory on which to evaluate robustness of property.
     * @return Robustness of property on trajectory.
     */
    R computeRobustness(P property, T trajectory);

}
