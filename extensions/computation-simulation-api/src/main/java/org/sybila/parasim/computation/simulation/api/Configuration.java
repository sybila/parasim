package org.sybila.parasim.computation.simulation.api;

import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.space.OrthogonalSpace;

/**
 * The simulation descriptor
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Configuration {

    /**
     * Returns number of dimensions of points on all trajectories to be
     * simulated.
     *
     * @return number of dimensions
     */
    int getDimension();

    /**
     * Returns space containing upper and lower bounds on all space-time dimensions.
     * The simulation of trajectory stops if the space border is reached.
     *
     * @return orthogonal space
     */
    OrthogonalSpace getSpace();

    /**
     * Returns the amount of work to do inside the simulate method
     * during the simulation of each trajectory
     *
     * @return amount of work to do inside the simulate method
     */
    int getMaxNumberOfIterations();

    /**
     * Returns ODE system which is used for the simulation
     *
     * @return ODE system
     */
    OdeSystem getOdeSystem();

    /**
     * Returns the maximum distance of two consecutive points
     * of one trajectory in all dimensions. If some dimension is not
     * to be checked the distance is set to 0.
     *
     * @return maximum distance of two consecutive points
     */
    float[] getSteps();

    /**
     * Returns the maximum time distance of two two consecutive points
     * of one trajectory.
     *
     * @return maximum time distance of two two consecutive points
     */
    float getTimeStep();
}