package org.sybila.parasim.core;

import java.lang.reflect.Field;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ContextEventPointImpl extends AbstractTypedField implements ContextEventPoint {

    public ContextEventPointImpl(Object target, Field field) {
        super(target, field);
    }

    public void set(ContextEvent<? extends Context> value) {
        try {
            if (!getField().isAccessible()) {
                getField().setAccessible(true);
            }
            getField().set(getTarget(), value);
        } catch(Exception e) {
            throw new InvocationException(e);
        }
    }
}
