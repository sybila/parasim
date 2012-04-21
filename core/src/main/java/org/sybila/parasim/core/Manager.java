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

import java.lang.annotation.Annotation;
import org.sybila.parasim.core.context.Context;

/**
 * Object which controls life cycle of all extensions and context. It also provides
 * sending events among the extensions
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Manager {

    /**
     * <p>Finalizes context:</p>
     * <ul>
     * <li>fires {@link org.sybila.parasim.core.event.After} in the parent context,</li>
     * <li>fires {@link org.sybila.parasim.core.event.After} in the given context,</li>
     * <li>destroys the given context,</li>
     * <li>drops all extension instances belonging to the context</li>
     * </ul>
     *
     * <p>Do not call this method directly use {@link org.sybila.parasim.core.ContextEvent} instead.</p>
     * @param context
     * @see {@link org.sybila.parasim.core.ContextEvent}
     */
    void finalizeContext(Context context);

    /**
     * Fires the event among all extensions belonging to the given context.
     *
     * @param event
     * @param context
     * @see {@link org.sybila.parasim.core.annotations.Observes}
     */
    void fire(Object event, Context context);

    /**
     * @return root context used by manager to store top level extensions and
     * instances
     */
    Context getRootContext();

    /**
     * <p>Initializes the given context:</p>
     * <ol>
     * <li>activates context,</li>
     * <li>fires {@link org.sybila.parasim.core.event.Before} in the parent context,</li>
     * <li>initializes all extensions belonging to the given context (depends on the scope),</li>
     * <li>binds providers of all extensions belonging to the given context,</li>
     * <li>fires {@link org.sybila.parasim.core.event.Before} in the given context and its parent</li>
     * </ol>
     * <p>Do not call this method directly, use {@link org.sybila.parasim.core.ContextEvent#initialize(org.sybila.parasim.core.context.Context) } instead.</p>
     * @param context
     * @see {@link org.sybila.parasim.core.ContextEvent}
     */
    void initializeContext(Context context);

    /**
     * Injects instance, event and context events to the given extension. Only
     * filed annotated by {@link org.sybila.parasim.core.Inject} annotation are considered.
     * @param target
     * @see {@link org.sybila.parasim.core.event.Instance}
     * @see {@link org.sybila.parasim.core.event.Event}
     * @see {@link org.sybila.parasim.core.event.ContextEvent}
     */
    void inject(Extension target);

    /**
     * Checks whether the manager is running
     */
    boolean isRunning();

    /**
     * Tries to resolve the instance determined by a pair of the type and qualifier.
     *
     * @param <T> type of the wanted instance
     * @param type of the wanted instance
     * @param qualifier of the wanted instance, if {@link org.sybila.parasim.core.annotations.Any} qualifier is used
     * @param context where the wanted instance should be placed, if the the context
     * doesn't contain the instance the parent context is used (if it's available)
     * @return
     */
    <T> T resolve(Class<T> type, Class<? extends Annotation> qualifier, Context context);

    /**
     * <p>Stops the manager:</p>
     * <ol>
     * <li>fires {@link org.sybila.parasim.core.event.ManagerStopping} event to the root {@link org.sybila.parasim.core.context.ApplicationContext},</li>
     * <li>finalizes all available contexts,</li>
     * <li>finalizes root {@link org.sybila.parasim.core.context.ApplicationContext},</li>
     * <li>drops all extensions,</li>
     * <li>drops all extension classes.</li>
     * </ol>
     */
    void shutdown();

    /**
     * <p>Starts the manager:</p>
     * <ol>
     * <li>fires {@link org.sybila.parasim.core.event.ManagerStarted} to the root {@link org.sybila.parasim.core.context.ApplicationContext}.</li>
     * <li>
     * </ol>
     */
    void start();

}
