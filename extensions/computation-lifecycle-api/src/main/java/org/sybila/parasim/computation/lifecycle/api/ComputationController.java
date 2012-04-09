package org.sybila.parasim.computation.lifecycle.api;

/**
 * Controls a computation from the workload point of view.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ComputationController {

    /**
     * @return the relative amount of work that is done in a computation batch
     *         during the call to the compute() method
     */
    Resources getSpeed();

    /**
     * @return descriptor of the computation process
     */
    ComputationStatus getStatus();

    /**
     * @param speed relative amount of work to do in future computation batches
     */
    void setSpeed(Resources speed);
}
