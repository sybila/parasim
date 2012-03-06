package org.sybila.parasim.core;

import org.sybila.parasim.core.context.Context;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InstanceImpl<T> implements Instance<T> {

    private ManagerImpl manager;
    private Context context;
    private Class<T> type;
    
    private InstanceImpl(Class<T> type, Context context, ManagerImpl manager) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [context] is null.");
        }
        this.manager = manager;
        this.type = type;
        this.context = context;
    }

    public static <T> Instance<T> of(Class<T> type, Context context, ManagerImpl manager) {
        return new InstanceImpl<T>(type, context, manager);
    }
    
    public T get() {
        return manager.resolve(type, context);
    }

    public void set(T instance) {
        manager.bind(type, context, instance);
    }
    
}
