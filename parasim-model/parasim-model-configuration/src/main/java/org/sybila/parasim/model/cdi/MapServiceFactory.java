package org.sybila.parasim.model.cdi;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapServiceFactory extends AbstractServiceFactory {

    private Map<Class<?>, Object> serviceInstantions = new HashMap<Class<?>, Object>();
    private Map<Class<?>, Class<?>> serviceImplementations = new HashMap<Class<?>, Class<?>>();
    
    @Override
    public Object getService(Class<?> interfaze, boolean fresh, Object... parameters) {
        if (fresh) {
            return createServiceInstance(interfaze, parameters);
        }
        if (serviceInstantions.get(interfaze) == null) {
            serviceInstantions.put(interfaze, createServiceInstance(interfaze));
        }
        return serviceInstantions.get(interfaze);
    }

    public void addService(Class<?> interfaze, Class<?> implementation) {
        serviceImplementations.put(interfaze, implementation);
    }

    public void addService(Class<?> interfaze, Object implementation) {
        serviceInstantions.put(interfaze, implementation);
    }

    public boolean isServiceAvailable(Class<?> interfaze) {
        return serviceImplementations.containsKey(interfaze) || serviceInstantions.containsKey(interfaze);
    }
    
    protected Object createServiceInstance(Class<?> interfaze, Object... parameters) {
        if (serviceImplementations.get(interfaze) == null) {
            return null;
        }
        Class<?> implementation = serviceImplementations.get(interfaze);
        return createInstance(implementation, parameters);
    }
    

}