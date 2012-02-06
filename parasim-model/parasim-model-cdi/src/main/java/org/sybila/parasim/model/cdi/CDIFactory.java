package org.sybila.parasim.model.cdi;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface CDIFactory {
    
    void addService(Class<?> interfaze, Class<?> implementation);

    void addService(Class<?> interfaze, Object implementation);
      
    Object getService(Class<?> interfaze);
    
    void injectFields(Object o);

    void executeVoidMethod(Object object, Method method);
    
    boolean isServiceAvailable(Class<?> interfaze);
}
