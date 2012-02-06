package org.sybila.parasim.model.cdi;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ServiceFactory {
    
    void addService(Class<?> interfaze, Class<?> implementation);

    void addService(Class<?> interfaze, Object implementation);
      
    Object getService(Class<?> interfaze);
    
    Object getService(Class<?> interfaze, boolean fresh, Object... parameters);
    
    void injectFields(Object o);

    void executeVoidMethod(Object object, Method method);
    
    boolean isServiceAvailable(Class<?> interfaze);
}
