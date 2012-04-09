package org.sybila.parasim.core;

import java.util.Collection;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Extension {

    Context getContext();

    Collection<ContextEventPoint> getContextEventPoints();

    Collection<InjectionPoint> getInjectionPoints();

    Collection<EventPoint> getEventPoints();

    Collection<ObserverMethod> getObservers();

    Collection<ProvidingPoint> getProvidingPoints();
}