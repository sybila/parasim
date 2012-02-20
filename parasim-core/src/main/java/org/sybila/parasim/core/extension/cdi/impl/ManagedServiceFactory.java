package org.sybila.parasim.core.extension.cdi.impl;

import org.sybila.parasim.core.Manager;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ManagedServiceFactory extends AbstractServiceFactory {

    private Manager manager;
    
    public ManagedServiceFactory(Manager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        this.manager = manager;
    }
    
    public <T> T getService(Class<T> type) {
        return manager.resolve(type);
    }

    public boolean isServiceAvailable(Class<?> type) {
        return manager.resolve(type) != null;
    }
    
}
