package org.sybila.parasim.core;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ProvidingFieldPoint extends AbstractTypedField implements ProvidingPoint {

    public ProvidingFieldPoint(Object target, Field field) {
        super(target, field);
    }

    public boolean fresh() {
        return false;
    }

    public Object value() {
        try {
            if (!getField().isAccessible()) {
                getField().setAccessible(true);
            }
            return getField().get(getTarget());
        } catch (IllegalArgumentException ex) {
            throw new InvocationException(ex);
        } catch (IllegalAccessException ex) {
            throw new InvocationException(ex);
        }
    }

}
