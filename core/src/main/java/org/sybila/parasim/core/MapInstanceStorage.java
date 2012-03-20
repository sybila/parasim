package org.sybila.parasim.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapInstanceStorage implements InstanceStorage {

    private Map<Class<?>, Object> instances;
    
    public MapInstanceStorage(Map<Class<?>, Object> instances) {
        if (instances == null) {
            throw new IllegalArgumentException("The parameter [instances] is null.");
        }
        this.instances = instances;
    }
    
    public MapInstanceStorage() {
        this(new ConcurrentHashMap<Class<?>, Object>());
    }
    
    public <T> InstanceStorage add(Class<T> type, T value) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter [value] is null.");
        }
        instances.put(type, value);
        return this;
    }

    public InstanceStorage clear() {
        instances.clear();
        return this;
    }

    public <T> T get(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }        
        return type.cast(instances.get(type));
    }
    
}