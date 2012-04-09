package org.sybila.parasim.core;

import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ContextEventImpl<C extends Context> implements ContextEvent<C> {

    private Context context;
    private Manager manager;

    private ContextEventImpl(Manager manager, Context context) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        this.manager = manager;
        this.context = context;
    }

    public static <T extends Context> ContextEvent<T> of(Class<T> contextClass, Manager manager, Context parentContext) {
        return new ContextEventImpl<T>(manager, parentContext);
    }

    public void initialize(C context) {
        context.setParent(this.context);
        manager.initializeContext(context);
    }

    public void finalize(C context) {
        context.setParent(this.context);
        manager.finalizeContext(context);
    }
}
