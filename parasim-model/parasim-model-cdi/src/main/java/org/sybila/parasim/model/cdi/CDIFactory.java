package org.sybila.parasim.model.cdi;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface CDIFactory {
    
    void addService(Class<?> interfaze, Class<?> implementation);

    void addService(Class<?> interfaze, Object implementation);
      
    void injectFields(Object o);
   
    boolean isServiceAvailable(Class<?> interfaze);
}
