package org.sybila.parasim.core;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface InjectionPoint extends Typed {
    
    Class<? extends Annotation> getScope();
    
    void set(Instance<?> value);
    
}
