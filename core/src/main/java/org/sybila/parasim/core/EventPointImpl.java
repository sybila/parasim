/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.core;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EventPointImpl extends AbstractTypedField implements EventPoint {

    public EventPointImpl(Object target, Field field) {
        super(target, field);
    }

    public void set(Event<?> value) throws InvocationException {
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
