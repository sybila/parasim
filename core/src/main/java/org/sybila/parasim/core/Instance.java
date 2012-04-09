package org.sybila.parasim.core;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Instance<I> extends Provider<I> {

    void set(I instance);

}
