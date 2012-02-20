package org.sybila.parasim.core.extension.cdi.impl;

import java.lang.reflect.Field;
import org.sybila.parasim.core.extension.cdi.api.ServiceFactory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class AbstractServiceFactory implements ServiceFactory {

    @Override
    public void injectFields(Object o) {
        for (Field field : o.getClass().getDeclaredFields()) {
            injectField(o, field);
        }
    }

    private void injectField(Object object, Field field) {
        Object service = getService(field.getType());
        if (service == null) {
            throw new IllegalStateException("The service " + field.getType().getName() + " requested in " + object.getClass().getName() + " is not available.");
        }
        field.setAccessible(true);
        try {
            field.set(object, field.getType().cast(service));
        } catch (Exception ex) {
            throw new IllegalStateException("The service can't be injected.", ex);
        }
    }
}
