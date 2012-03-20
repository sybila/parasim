package org.sybila.parasim.core;

import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ContextEvent<C extends Context> {

    void initialize(C context);
    
    void finalize(C context);
    
}
