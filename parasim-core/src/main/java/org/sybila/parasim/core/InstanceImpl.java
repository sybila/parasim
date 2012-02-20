package org.sybila.parasim.core;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InstanceImpl<T> implements Instance<T> {

    private ManagerImpl manager;
    private Class<? extends Annotation> scope;
    private Class<T> type;
    
    private InstanceImpl(Class<T> type, Class<? extends Annotation> scope, ManagerImpl manager) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (scope == null) {
            throw new IllegalArgumentException("The parameter [scope] is null.");
        }
        this.manager = manager;
        this.type = type;
        this.scope = scope;
    }

    public static <T> Instance<T> of(Class<T> type, Class<? extends Annotation> scope, ManagerImpl manager) {
        return new InstanceImpl<T>(type, scope, manager);
    }
    
    public T get() {
        return manager.resolve(type);
    }

    public void set(T instance) {
        manager.bind(type, scope, instance);
    }
    
}
