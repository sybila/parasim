package org.sybila.parasim.computation.simulation;

/**
 * Adaptive step simulation descriptor
 * 
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface AdaptiveStepConfiguration extends Configuration {

    /**
     * Returns maximal absolute error which has to be kept by the simulation
     * 
     * @return maximal absolute error per dimension
     */
    float[] getMaxAbsoluteError();

    /**
     * Returns maximal relative error which has to be kept by the simulation
     * 
     * @return maximal relative error per dimension
     */
    float[] getMaxRelativeError();
}
