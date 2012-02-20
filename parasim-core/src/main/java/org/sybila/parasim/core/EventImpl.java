package org.sybila.parasim.core;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class EventImpl<E> implements Event<E> {

    private Manager manager;
    
    private EventImpl(Manager manager) {
        this.manager = manager;
    }
    
    public static <T> Event<T> of(Class<T> type, Manager manager) {
        if (type == null) {
            throw new IllegalArgumentException("The parameter [type] is null.");
        }
        if (manager == null) {
            throw new IllegalArgumentException("The parameter [manager] is null.");
        }
        return new EventImpl<T>(manager);
    }
    
    public void fire(E event) {
        manager.fire(event);
    }

}
