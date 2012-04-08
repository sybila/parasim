package org.sybila.parasim.core.context;

import org.sybila.parasim.core.InstanceStorage;
import org.sybila.parasim.core.MapInstanceStorage;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractContext implements Context {

    private boolean activity = false;
    private InstanceStorage instanceStorage;
    private Context parent;
    
    public AbstractContext(InstanceStorage instanceStorage) {
        if (instanceStorage == null) {
            throw new IllegalArgumentException("The parameter [instanceStorage] is null.");
        }
        this.instanceStorage = instanceStorage;
    }

    public AbstractContext() {
        this(new MapInstanceStorage());
    }
 
    public void activate() {
        activity = true;
    }

    public void deactivate() {
        activity = false;
    }    
    
    public void destroy() {
        instanceStorage.clear();
        instanceStorage = null;
    }

    public Context getParent() {
        return parent;
    }
    
    public InstanceStorage getStorage() {
        return instanceStorage;
    }
   
    public boolean hasParent() {
        return parent != null;
    }

    public boolean isActive() {
        return activity;
    }    
    
    public void setParent(Context context) {
        parent = context;
    }
    
    public <T> T resolve(Class<T> type) {
        // the given context has priority
        if (this.isActive()) {
            T value = this.getStorage().get(type);
            if (value != null) {
                return value;
            }
        }
        if (this.hasParent()) {
            return this.getParent().resolve(type);
        }
        // nothing found
        return null;
    }
}
