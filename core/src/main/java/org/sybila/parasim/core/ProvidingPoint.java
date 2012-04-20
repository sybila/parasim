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

/**
 * Part of the extensions which can be used as an provider. This providing points
 * are searched by {@link org.sybila.parasim.core.annotations.Provide} annotation
 * before fields and methods.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ProvidingPoint extends Typed {

    /**
     * Determines whether the provider will call the providing point every time,
     * when someone calls the provided instance.
     *
     * @return TRUE if the provider should call the providing point when someone
     * calls the provided instance, FALSE when provider is enabled to cache.
     */
    boolean fresh();

    /**
     * @return provided instance
     */
    Object value();
}
