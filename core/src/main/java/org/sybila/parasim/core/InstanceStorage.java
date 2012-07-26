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
import org.sybila.parasim.core.spi.InstanceCleaner;

/**
 * The storage is used by contexts to control instances belonging to the context.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface InstanceStorage {

    /**
     * Adds an instance to the storage. The key for the given instance is a pair
     * of the given type and given qualifier.
     *
     * @param <T> type used for retrieving instance from the storage
     * @param type used for retrieving instance from the storage
     * @param qualifier used for retrieving instance from the storage
     * @param value the given instance
     * @return
     */
    <T> InstanceStorage add(Class<T> type, Class<? extends Annotation> qualifier, T value);

    /**
     * Adds an instance cleaner which is called when the storage is cleared.
     *
     * @param cleaner
     */
    void addInstanceCleaner(InstanceCleaner cleaner);

    /**
     * Clears the storage. It means that all references to instances are dropped.
     * @return
     */
    InstanceStorage clear();

    /**
     * Retrieves an instance which has been already added to the storage.
     *
     * @param <T> type of the wanted instance
     * @param type of the wanted instance
     * @param qualifier of the wanted instance, if {@link org.sybila.parasim.core.annotations.Any} qualifier is used
     * then qualifier isn't considered
     * @return
     * @throws org.sybila.parasim.core.AmbigousException if there are more than one candidates to retrieve
     */
    <T> T get(Class<T> type, Class<? extends Annotation> qualifier);
}
