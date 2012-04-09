package org.sybila.parasim.core;

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ObserverMethod extends Typed {

    void invoke(Manager manager, Object event);

    Method getMethod();

}
