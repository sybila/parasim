/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package parasim;

/**
 * 
 * @author sven
 */
public interface ComputationInterface {

    /** 
     * @return number of miliseconds computation took
     */
    long compute();

    /**
     * @return true if computation has finished
     */
    boolean finished();

    /**
     * @return the relative amount of work that is done in a computation batch
     *         during the call to the compute() method
     */
    float getSpeed();

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
     * @return Total time of comuptation in miliseconds.
     */
    long getTotalTime();

}
