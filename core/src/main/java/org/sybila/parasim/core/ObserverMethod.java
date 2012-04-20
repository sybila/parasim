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

import java.lang.reflect.Method;

/**
 * Methods observing events. These methods are searched in extension classes
 * by {@link org.sybila.parasim.core.annotations.Observes} annotation before its first argument.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ObserverMethod extends Typed {

    /**
     * Invokes the method. The first argument has to be an observing event object.
     * It tries to resolve all arguments using the given manager, if there is an
     * argument which can't be resolved, the method fails.
     *
     * @param manager
     * @param event
     * @throws IllegalStateException if the method arguments can't be resolved
     */
    void invoke(Manager manager, Object event);

    /**
     * @return the method object
     */
    Method getMethod();

}
