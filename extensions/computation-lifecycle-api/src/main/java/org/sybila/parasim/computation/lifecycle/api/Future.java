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
package org.sybila.parasim.computation.lifecycle.api;

import java.util.concurrent.ExecutionException;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Future<V> extends java.util.concurrent.Future<V> {

    /**
     * Returns a computation status.
     */
    Status getStatus();

    /**
     * Returns a partial result. The partial result can be returned only
     * when the computation has been forked to more computation instances and
     * some of the instances has already finished.
     *
     * @return partial result or null if there is no partial result available
     */
    V getPartial() throws InterruptedException, ExecutionException;;

}
