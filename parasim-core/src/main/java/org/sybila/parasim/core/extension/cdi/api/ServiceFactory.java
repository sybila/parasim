package org.sybila.parasim.core.extension.cdi.api;

import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ServiceFactory {

    <T> T getService(Class<T> type, Context context);
        
    void injectFields(Object target, Context context);
    
    boolean isServiceAvailable(Class<?> type, Context context);
    
}
