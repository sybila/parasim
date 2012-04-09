package org.sybila.parasim.core;

import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EventImpl<E> implements Event<E> {

    private Context context;
    private Manager manager;

    private EventImpl(Manager manager, Context context) {
        this.manager = manager;
        this.context = context;
    }

    public static <T> Event<T> of(Class<T> type, Context context, Manager manager) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        if (context == null) {
            throw new IllegalArgumentException("The parameter [contex] is null.");
        }
        return new EventImpl<T>(manager, context);
    }

    public void fire(E event) {
        manager.fire(event, context);
    }

}
