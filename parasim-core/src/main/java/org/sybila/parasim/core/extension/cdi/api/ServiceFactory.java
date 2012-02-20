package org.sybila.parasim.core.extension.cdi.api;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ServiceFactory {

    <T> T getService(Class<T> type);
        
    void injectFields(Object target);
    
    boolean isServiceAvailable(Class<?> type);
    
}
