package org.sybila.parasim.model.cdi;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class MapCDIFactory extends AbstractCDIFactory {

    private Map<Class<?>, Object> serviceInstantions = new HashMap<Class<?>, Object>();
    private Map<Class<?>, Class<?>> serviceImplementations = new HashMap<Class<?>, Class<?>>();
    
    @Override
    public Object getService(Class<?> interfaze) {
        if (serviceInstantions.get(interfaze) == null) {
            if (serviceImplementations.get(interfaze) == null) {
                return null;
            }
            Class<?> implementation = serviceImplementations.get(interfaze);
            for (Constructor construtor : implementation.getConstructors()) {
                if (construtor.getParameterTypes().length > 0) {
                    continue;
                }
                try {
                    serviceInstantions.put(interfaze, construtor.newInstance());
                } catch (Exception e) {
                    throw new IllegalStateException("The implementation " + implementation.getClass().getCanonicalName() + " of service " + interfaze.getCanonicalName() + " can't be instantiated.", e);
                }
            }
            if (serviceImplementations.get(interfaze) == null) {
                throw new IllegalStateException("The implementation " + implementation.getClass().getCanonicalName() + " of service " + interfaze.getCanonicalName() + " hasn't any constructor without parameters.");
            }
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
    
}
