package org.sybila.parasim.core.context;

import org.sybila.parasim.core.InstanceStorage;
import org.sybila.parasim.core.MapInstanceStorage;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractContext implements Context {

    private InstanceStorage instanceStorage;
    
    public AbstractContext(InstanceStorage instanceStorage) {
        if (instanceStorage == null) {
            throw new IllegalArgumentException("The parameter [instanceStorage] is null.");
        }
        this.instanceStorage = instanceStorage;
    }

    public AbstractContext() {
        this(new MapInstanceStorage());
    }
    
    public void destroy() {
        instanceStorage.clear();
        instanceStorage = null;
    }

    public InstanceStorage getStorage() {
        return instanceStorage;
    }
   
}
