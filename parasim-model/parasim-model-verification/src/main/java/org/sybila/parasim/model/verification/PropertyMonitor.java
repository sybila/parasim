package org.sybila.parasim.model.verification;

import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * Represents a method to verify a property on the trajectory of an OdeSystem
 * or to compute its robustness.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface PropertyMonitor<P extends Property, T extends Trajectory, R extends PropertyRobustness>
{
    /**
     * Computes the validity of the property on the trajectory.
     *
     * @param property Property to verify on trajectory.
     * @param trajectory Trajectory on which to verify the property.
     * @return True if property holds on trajectory, else false.
     */
    boolean computeValidity(P property, T trajectory);

    /**
     * Computes the robustness of the property on the trajectory.
     *
     * @param property Property who's robustness to compute on trajectory.
     * @param trajectory Trajectory on which to evaluate robustness of property.
     * @return Robustness of property on trajectory.
     */
    R computeRobustness(P property, T trajectory);
}
