package org.sybila.parasim.core.event;

import org.sybila.parasim.core.Manager;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ManagerProcessing {
        
    void context(Class<? extends Context> context);
    
    void extension(Class<?> extension);
    
    Manager getManager();
}
