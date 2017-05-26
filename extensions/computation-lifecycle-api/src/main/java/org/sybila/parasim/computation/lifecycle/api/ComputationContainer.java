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

import org.sybila.parasim.model.Mergeable;

/**
 * The entry point to compute your computation instance. Computation container
 * is able to read annotations defined in your computation to configure its execution.
 *
 * @see org.sybila.parasim.computation.lifecycle.api.annotation.RunWith
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface ComputationContainer {

    /**
     * Performs the execution of the given computation instance.
     *
     * @param <Result> type of the result
     * @return computed result
     */
    <Result extends Mergeable<Result>> Future<Result> compute(Computation<Result> computation);
}