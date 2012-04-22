/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.core;

import java.util.Collection;
import org.sybila.parasim.core.context.Context;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Extension {

    /**
     * Context where the extensions is placed. The context is searched by
     * {@link org.sybila.parasim.core.annotations.Scope}
     * annotation placed before the extension class.
     *
     * @return the context where the extension is placed.
     */
    Context getContext();

    /**
     * Points which can be used to inject context event objects. These points
     * are searched by {@link org.sybila.parasim.core.Inject} annotation placed
     * before the {@link org.sybila.parasim.core.ContextEvent}
     * class fields.
     *
     * @return context events points
     */
    Collection<ContextEventPoint> getContextEventPoints();

    /**
     * Points which can be used to inject instances objects. These points are
     * searched by {@link org.sybila.parasim.core.annotations.Inject} annotation
     * placed before the {@link org.sybila.parasim.core.Instance} class fields.
     *
     * @return injection points
     */
    Collection<InjectionPoint> getInjectionPoints();

    /**
     * Points which can be used to inject event objects. These points are searched
     * by {@link org.sybila.parasim.core.annotations.Inject} annotation placed before the {@link org.sybila.parasim.core.Event}
     * class fields.
     *
     * @return event points
     */
    Collection<EventPoint> getEventPoints();

    /**
     * Methods observing some events. These observers are searched by
     * {@link org.sybila.parasim.core.annotations.Observes}
     * annotation placed before the first method parameter.
     *
     * @return methods observing some events
     */
    Collection<ObserverMethod> getObservers();

    /**
     * Methods and fields providing service instances. These providers are searched
     * by {@link org.sybila.parasim.core.annotations.Provide} annotation placed
     * before the class field or method.
     *
     * @return extension providers
     */
    Collection<ProvidingPoint> getProvidingPoints();
}