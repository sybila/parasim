package org.sybila.parasim.core;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Instance<I> {
    
    I get();
    
    void set(I instance);
    
}
