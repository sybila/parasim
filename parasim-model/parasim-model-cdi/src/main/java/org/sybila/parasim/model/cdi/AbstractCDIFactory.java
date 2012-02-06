package org.sybila.parasim.model.cdi;

import java.lang.reflect.Field;
import org.sybila.parasim.model.cdi.annotations.Inject;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractCDIFactory implements CDIFactory {
    
    public void injectFields(Object o) {
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Inject.class) != null) {
                Object service = getService(field.getType());
                if (service == null) {
                    throw new IllegalStateException("The service " + field.getType().getCanonicalName() + " requested in " + o.getClass().getCanonicalName() + " is not available.");
                }
                field.setAccessible(true);
                try {
                    field.set(o, field.getType().cast(service));
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("The service can't be injected.", ex);
                }
            }
        }
    }
    
    protected abstract Object getService(Class<?> interfaze);
    
}
