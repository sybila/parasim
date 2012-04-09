package org.sybila.parasim.core;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface InjectionPoint extends Typed {

    void set(Instance<?> value);

}
