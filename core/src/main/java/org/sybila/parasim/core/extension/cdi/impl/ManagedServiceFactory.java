package org.sybila.parasim.core.extension.cdi.impl;

import org.sybila.parasim.core.ManagerImpl;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ManagedServiceFactory extends AbstractServiceFactory {

    private ManagerImpl manager;

    public ManagedServiceFactory(ManagerImpl manager) {
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        this.manager = manager;
    }

    public <T> T getService(Class<T> type, Context context) {
        return manager.resolve(type, context);
    }

    public boolean isServiceAvailable(Class<?> type, Context context) {
        return manager.resolve(type, context) != null;
    }

    @Override
    protected <T> void bind(Class<T> clazz, Context context, Object value) {
        manager.bind(clazz, context, (T) value);
    }
}
