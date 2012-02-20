package org.sybila.parasim.core.context;

import java.lang.annotation.Annotation;
import org.sybila.parasim.core.InstanceStorage;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Context {

    void activate();
    
    void deactivate();
    
    void destroy();
    
    Class<? extends Annotation> getScope();
    
    InstanceStorage getStorage();
    
    boolean isActive();
    
}
