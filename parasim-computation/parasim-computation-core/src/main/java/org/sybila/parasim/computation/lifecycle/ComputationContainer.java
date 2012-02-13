package org.sybila.parasim.computation.lifecycle;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ComputationContainer {
    
    void finalize(Computation computation);
    
    void init(Computation computation);
    
    void stop(Computation computation);
    
    void start(Computation computation);
    
}
