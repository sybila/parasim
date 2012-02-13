package org.sybila.parasim.computation.lifecycle;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ComputationStatus {
    
    boolean isFinalized();
    
    boolean isInitialized();
    
    boolean isFinished();
    
    boolean isStarted();
    
    boolean isRunning();
    
    long getLastConsumedTime();
    
    long getTotalConsumedTime();
    
}
