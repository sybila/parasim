package org.sybila.parasim.core;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InjectionPointImpl extends AbstractTypedField implements InjectionPoint {

    public InjectionPointImpl(Object target, Field field) {
        super(target, field);
    }

    public void set(Instance<?> value) {
        try {
            if (!getField().isAccessible()) {
                getField().setAccessible(true);
            }
            getField().set(getTarget(), value);
        } catch (Exception e) {
            throw new InvocationException(e);
        }
    }

}