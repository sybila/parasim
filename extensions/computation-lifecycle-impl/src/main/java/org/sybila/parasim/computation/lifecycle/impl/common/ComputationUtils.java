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
package org.sybila.parasim.computation.lifecycle.impl.common;

import org.sybila.parasim.computation.lifecycle.api.Computation;
import org.sybila.parasim.computation.lifecycle.api.Selector;
import org.sybila.parasim.computation.lifecycle.api.annotations.RunWith;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class ComputationUtils {

    private ComputationUtils() {
    }

    public static Selector<Computation> getBalancerSelector(Class<? extends Computation> clazz) {
        if (clazz.getAnnotation(RunWith.class) != null && clazz.getAnnotation(RunWith.class).balancer() != null) {
            return (Selector<Computation>) instantiate(clazz.getAnnotation(RunWith.class).balancer());
        } else {
            return instantiate(RunWith.DefaultGeneralSelector.class);
        }
    }

    public static Selector<Computation> getOffererSelector(Class<? extends Computation> clazz) {
        if (clazz.getAnnotation(RunWith.class) != null && clazz.getAnnotation(RunWith.class).offerer() != null) {
            return (Selector<Computation>) instantiate(clazz.getAnnotation(RunWith.class).offerer());
        } else {
            return instantiate(RunWith.DefaultGeneralSelector.class);
        }
    }

    private static <T> T instantiate(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Can't create a new instance of " + clazz.getName() + ".", e);
        }
    }

}
