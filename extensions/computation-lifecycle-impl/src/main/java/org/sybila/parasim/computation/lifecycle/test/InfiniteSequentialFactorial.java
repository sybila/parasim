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
package org.sybila.parasim.computation.lifecycle.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.core.annotation.Inject;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class InfiniteSequentialFactorial implements Computation<MultiplicativeInteger> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfiniteSequentialFactorial.class);

    private final Integer n;
    @Inject
    private Emitter emitter;

    public InfiniteSequentialFactorial(Integer n) {
        this.n = n;
    }

    @Override
    public MultiplicativeInteger call() throws Exception {
        LOGGER.debug(this + " started");
        if (n > 1) {
            emitter.emit(new InfiniteSequentialFactorial(n - 1));
        } else {
            synchronized (this) {
                wait();
            }
        }
        LOGGER.debug(this + " finished");
        return new MultiplicativeInteger(n);
    }

    @Override
    public void destroy() throws Exception {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " ### " + n;
    }
}
