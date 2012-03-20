package org.sybila.parasim.computation.lifecycle.api;

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
    
    void setFinalized();

    void setFinished();
    
    void setInitialized();
    
    void startRunning();
    
    void stopRunning();
}