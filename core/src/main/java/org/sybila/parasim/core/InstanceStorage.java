package org.sybila.parasim.core;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface InstanceStorage {
    
    <T> InstanceStorage add(Class<T> type, T value);
    
    InstanceStorage clear();
    
    <T> T get(Class<T> type);
}
