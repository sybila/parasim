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

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.Emitter;
import org.sybila.parasim.core.annotation.Inject;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class ParallelFactorial implements Computation<MultiplicativeInteger> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelFactorial.class);

    private final Integer n;
    private final boolean cloned;
    @Inject
    private Emitter emitter;

    public ParallelFactorial(Integer n) {
        this.n = n;
        this.cloned = false;
    }

    public ParallelFactorial(Integer n, boolean cloned) {
        this.n = n;
        this.cloned = cloned;
    }

    @Override
    public MultiplicativeInteger call() throws Exception {
        LOGGER.debug(this + " started");
        if (n > 1 && !cloned) {
            for (int i = 1; i < n; i++) {
                emitter.emit(new ParallelFactorial(i, true));
            }
        }
        long toSleep;
        synchronized(Random.class) {
            toSleep = new Random().nextInt(200);
        }
        Thread.sleep(toSleep);
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
