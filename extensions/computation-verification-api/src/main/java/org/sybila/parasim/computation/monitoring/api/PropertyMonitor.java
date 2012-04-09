package org.sybila.parasim.computation.monitoring.api;

import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.model.verification.Property;

/**
 * Represents a method to verify a property on a trajectory of an OdeSystem.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public interface PropertyMonitor<P extends Property, T extends Trajectory, R extends PropertyRobustness> {
    /**
     * Computes the validity of the property on the trajectory.
     *
     * @param property Property to verify on trajectory.
     * @param trajectory Trajectory on which to verify the property.
     * @return True if property holds on trajectory, else false.
     */
    boolean computeValidity(P property, T trajectory);
}
