package org.sybila.parasim.core.event;

import org.sybila.parasim.core.Manager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ManagerProcessing {
    
    void extension(Class<?> extension);
    
    Manager getManager();
}
