package org.sybila.parasim.core.extension.cdi.api;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface InstanceProducer<T> {
    
    T create();
    
}
