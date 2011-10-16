package org.sybila.parasim.computation.density;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Distance {
    
    float getMaxDistance();
    
    float getDimensionDistance(int dimensionIndex);
    
}
