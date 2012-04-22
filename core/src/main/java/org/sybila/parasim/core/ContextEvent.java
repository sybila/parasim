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

import org.sybila.parasim.core.context.Context;

/**
 * The events to control context life cycle.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ContextEvent<C extends Context> {

    /**
     * <p>Initializes the given context:</p>
     * <ol>
     * <li>sets parent (parent is context where the extension which fires the event is placed),</li>
     * <li>activates context,</li>
     * <li>fires {@link org.sybila.parasim.core.event.Before} in the parent context,</li>
     * <li>initializes all extensions belonging to the given context (depends on the scope),</li>
     * <li>binds providers of all extensions belonging to the given context,</li>
     * <li>fires {@link org.sybila.parasim.core.event.Before} in the given context and its parent</li>
     * </ol>
     * @param context
     */
    void initialize(C context);

    /**
     * <p>Finalizes the given context:</p>
     * <ol>
     * <li>sets parent (parent is context where the extension which fires the event is placed),</li>
     * <li>fires {@link org.sybila.parasim.core.event.After} in the parent context,</li>
     * <li>fires {@link org.sybila.parasim.core.event.After} in the given context,</li>
     * <li>destroys the given context,</li>
     * <li>drops all extension instances belonging to the context</li>
     * </ol>
     * @param context
     */
    void finalize(C context);

}
