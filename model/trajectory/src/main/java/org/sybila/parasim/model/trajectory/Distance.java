package org.sybila.parasim.model.trajectory;

/**
 * Represents distance between two objects.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Distance {    

    /**
     * Returns the overall distance of two objects as a real number.
     * @return Distance as a real number.
     */
    float value();
    
}
