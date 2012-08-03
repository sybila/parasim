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
package org.sybila.parasim.core.context;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import org.sybila.parasim.core.InstanceStorage;
import org.sybila.parasim.core.spi.InstanceCleaner;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Context extends Serializable {

    /**
     * Activates the context. If the context isn't active, it can't be used
     * for resolving the instances. Don't use this method directly,
     * use {@link org.sybila.parasim.core.ContextEvent#initialize(org.sybila.parasim.core.context.Context) }
     * instead.
     * @see {@link org.sybila.parasim.core.Manager#resolve(java.lang.Class, java.lang.Class, org.sybila.parasim.core.context.Context) }
     */
    void activate();

    /**
     * Adds an instance cleaner which is called when the context is destroyed.
     *
     * @param cleaner
     */
    void addInstanceCleaner(InstanceCleaner cleaner);

    /**
     * Deactivates the context. If the context isn't active, it can't be used
     * for resolving the instances. Don't use this method directly,
     * use {@link org.sybila.parasim.core.ContextEvent#finalize(org.sybila.parasim.core.context.Context) }
     * instead.
     * @see {@link org.sybila.parasim.core.Manager#resolve(java.lang.Class, java.lang.Class, org.sybila.parasim.core.context.Context) }
     */
    void deactivate();

    /**
     * Destroys the context. It means that the context drops references on its instances.
     * Don't use this method directly,
     * use {@link org.sybila.parasim.core.ContextEvent#finalize(org.sybila.parasim.core.context.Context) }
     * instead.
     */
    void destroy();

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
     * @return storage where the instances are stored, you shouldn't use this method
     * directly
     * @see {@link org.sybila.parasim.core.Manager}
     * @see {@link org.sybila.parasim.core.extension.cdi.api.ServiceFactory#provideFieldsAndMethods(java.lang.Object, org.sybila.parasim.core.context.Context) }
     * @see {@link org.sybila.parasim.core.annotations.Inject}
     * @see {@link org.sybila.parasim.core.annotations.Provide}
     */
    InstanceStorage getStorage();

    /**
     * @return checks whether the context has a parent
     */
    boolean hasParent();

    /**
     * If the context isn't active, it can't be used for resolving the instances.
     *
     * @return checks whether the context is active
     */
    boolean isActive();

    /**
     * Sets a parent of the context. Don't use this method directly. use
     * {@link org.sybila.parasim.core.ContextEvent#initialize(org.sybila.parasim.core.context.Context) }
     * instead.
     *
     * @param context
     */
    void setParent(Context context);

    /**
     * Resolves an instance. You shouldn't use this method directly, use
     * {@link org.sybila.parasim.core.annotations.Inject} annotation and
     * {@link org.sybila.parasim.core.extension.cdi.api.ServiceFactory#injectFields(java.lang.Object, org.sybila.parasim.core.context.Context) }
     * or {@link org.sybila.parasim.core.Manager#resolve(java.lang.Class, java.lang.Class, org.sybila.parasim.core.context.Context) }
     * @param <T> type of the wanted instance
     * @param type type of the wanted instance
     * @param qualifier qualifier of the wanted instance
     * @return the wanted instance or null, if the instance can't be resolved
     */
    <T> T resolve(Class<T> type, Class<? extends Annotation> qualifier);
}
