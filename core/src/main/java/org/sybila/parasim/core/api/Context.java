/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.core.api;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Context extends Serializable, Destroyable, Resolver, EventDispatcher, ContextFactory {

    /**
     * @return parent context which is used to resolve instances which can't be
     * resolving using this context
     */
    Context getParent();

    /**
     * @return scope which is used to determine which context is used for extensions
     * @see {@link org.sybila.parasim.core.annotations.Scope}
     */
    Class<? extends Annotation> getScope();

    /**
     * @return checks whether the context has a parent
     */
    boolean hasParent();
}
