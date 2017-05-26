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
package org.sybila.parasim.computation.lifecycle.api.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.Executor;
import org.sybila.parasim.computation.lifecycle.api.Selector;

/**
 * Defines a configuration for the given computation.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RunWith {

    /**
     * Specifies the executor used for execution.
     *
     * @see org.sybila.parasim.computation.lifecycle.api.SharedMemoryExecutor
     * @see org.sybila.parasim.computation.lifecycle.api.DistributedMemoryExecutor
     */
    Class<? extends Executor> executor() default Executor.class;

    Class<? extends Selector<? extends Computation>> balancer() default DefaultSelector.class;

    Class<? extends Selector<? extends Computation>> offerer() default DefaultSelector.class;

    public static class DefaultGeneralSelector<C extends Computation> implements Selector<C> {

        @Override
        public C select(Collection<C> items) {
            return items.iterator().next();
        }

    }

    public static class DefaultSelector extends DefaultGeneralSelector<Computation> {

        @Override
        public Computation select(Collection<Computation> items) {
            return super.select(items);
        }

    }

}
