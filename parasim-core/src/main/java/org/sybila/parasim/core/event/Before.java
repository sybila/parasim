package org.sybila.parasim.core.event;

import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Before<C extends Context> {
    
    private C context;
    
    private Before(C context) {
        if (context == null) {
            throw new IllegalArgumentException("The parameter context is null.");
        }
        this.context = context;
    }
    
    public static <T extends Context> Before<T> of(T context) {
        return new Before<T>(context);
    }
    
    public C getContext() {
        return context;
    }
    
}
