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
package org.sybila.parasim.computation.cycledetection.cpu;

/**
 * Specifies which extreme values are to be remembered.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 */
public enum ExtremesMode {

    EXTREME_NONE,
    EXTREME_MIN,
    EXTREME_MAX,
    EXTREME_BOTH;

    public static ExtremesMode fromInt(int status) {
    switch(status) {
            case 0:
                return EXTREME_NONE;
            case 1:
        return EXTREME_MIN;
            case 2:
        return EXTREME_MAX;
            case 3:
        return EXTREME_BOTH;
            default:
        throw new IllegalStateException("There is no mode corresponding to the number [" + status + "].");
        }
    }

}