package org.sybila.parasim.computation;

/**
 * Controls a computation from the workload point of view.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public interface ComputationController
{
    /**
     * @return number of miliseconds computation took
     */
    long compute();

    /**
     * @return true if computation has finished
     */
    boolean finished();    

    /**
     * Pauses the computation.
     */
    void pause();

    /**
     * @return true if computation is paused
     */
    boolean paused();

    /**
     * Resumes computation.
     */
    void resume();

    /**
     * @param newSpeed relative amount of work to do in future computation batches
     */
    void setSpeed(float newSpeed);

    /**
     * @return the relative amount of work that is done in a computation batch
     *         during the call to the compute() method
     */
    float getSpeed();

    /**
     * @return Total time of comuptation in miliseconds.
     */
    long getTotalTime();

}
