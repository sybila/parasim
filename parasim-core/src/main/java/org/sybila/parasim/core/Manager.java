package org.sybila.parasim.core;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Manager {
    
    void fire(Object event);
    
    void inject(Object o);
    
    <T> T resolve(Class<T> type);
    
    void shutdown();
    
    void start();
    
}
