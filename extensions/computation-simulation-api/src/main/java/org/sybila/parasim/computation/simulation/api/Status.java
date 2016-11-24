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
package org.sybila.parasim.computation.simulation.api;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public enum Status {

    OK, BOUNDS, PRECISION, TIMEOUT;

    /**
     * Converts an integer to the simulation status
     * @return simulation status
     *
     * @throws IllegalArgumentException if the given integer doesn't correspond
     * to any simulation status
     */
    public static Status fromInt(int status) {
        switch (status) {
            case 0:
                return OK;
            case 1:
                return BOUNDS;
            case 2:
                return PRECISION;
            case 3:
                return TIMEOUT;

            default:
                throw new IllegalStateException("There is no status corresponding to the number [" + status + "].");
        }
    }
}
