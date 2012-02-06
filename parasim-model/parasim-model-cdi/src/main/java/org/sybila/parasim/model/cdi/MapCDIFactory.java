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
    
    private Object createServiceInstance(Class<?> interfaze, Object... parameters) {
        if (serviceImplementations.get(interfaze) == null) {
            return null;
        }
        Class<?> implementation = serviceImplementations.get(interfaze);
        findConstructor : for (Constructor construtor : implementation.getConstructors()) {
            if (construtor.getParameterTypes().length != parameters.length) {
                continue findConstructor;
            }
            for (int i=0; i<parameters.length; i++) {
                if (!construtor.getParameterTypes()[i].isInstance(parameters[i])) {
                    continue findConstructor;
                }
            }
            try {
                return construtor.newInstance(parameters);
            } catch (Exception e) {
                throw new IllegalStateException("The implementation " + implementation.getCanonicalName() + " of service " + interfaze.getCanonicalName() + " can't be instantiated.", e);
            }
        }
        throw new IllegalStateException("The implementation " + implementation.getCanonicalName() + " of service " + interfaze.getCanonicalName() + " hasn't any constructor with the given parameters");
    }
    
}