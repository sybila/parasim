package org.sybila.parasim.core;

import java.util.Collection;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Extension {
    
    Collection<InjectionPoint> getInjectionPoints();
    
    Collection<EventPoint> getEventPoints();
    
    Collection<ObserverMethod> getObservers();
    
}