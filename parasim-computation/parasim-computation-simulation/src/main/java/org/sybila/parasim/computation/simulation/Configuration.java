package org.sybila.parasim.computation.simulation;

import org.sybila.parasim.model.ode.OdeSystem;

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
     * Returns the upper bounds on all space-time dimensions.
     * The simulation of a trajectory stops if this value is reached.
     * 
     * @return upper bounds
     */
    float[] getMaxBounds();

    /**
     * Returns the amount of work to do inside the simulate method
     * during the simulation of each trajectory
     * 
     * @return amount of work to do inside the simulate method
     */
    int getMaxNumberOfIterations();

    /**
     * Returns the lower bounds on all space-time dimensions.
     * The simulation of a trajectory stops if this value is reached.
     * 
     * @return lower bounds on all space-time dimensions
     */
    float[] getMinBounds();

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
     * Returns the maximum time. The simulation of a trajectory stops if this time is
     * reached.
     * 
     * @return maximum time
     */
    float getTargetTime();

    /**
     * Returns the maximum time distance of two two consecutive points
     * of one trajectory.
     * 
     * @return maximum time distance of two two consecutive points
     */
    float getTimeStep();
}
