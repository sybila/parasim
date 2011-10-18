package org.sybila.parasim.computation.density;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Distance {
    
    boolean isValid();
    
    boolean isValid(int dimensionIndex);
    
    float value();
    
    float value(int dimensionIndex);
    
}
