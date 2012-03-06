package org.sybila.parasim.core;

import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ContextEventImpl<C extends Context> implements ContextEvent<C> {

    private Manager manager;
    
    private ContextEventImpl(Manager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        this.manager = manager;
    }
    
    public static <T extends Context> ContextEvent<T> of(Class<T> context, Manager manager) {
        return new ContextEventImpl<T>(manager);
    }
    
    public void initialize(C context) {
        manager.initializeContext(context);
    }

    public void finalize(C context) {
        manager.finalizeContext(context);
    }
    
}
