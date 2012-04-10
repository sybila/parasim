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
package org.sybila.parasim.model.verification.buchi;

import org.sybila.parasim.model.trajectory.Point;

/**
 *
 * @author Sven Drazan <sven@mail.muni.cz>
 */
public class Transition {

    private int from, to;  /* origin and target state */

    private Evaluable guard; /* condition guarding the transition */


    public Transition(int from, int to, Evaluable guard) {
        this.from = from;
        this.to = to;
        this.guard = guard;
    }

    boolean enabled(Point p) {
        if (guard == null) {
            /* Some transitions have empty guards which are always true */
            return true;
        } else {
            return guard.valid(p);
        }
    }

    /**
     * @return the origin state
     */
    public int getFrom() {
        return from;
    }

    /**
     * @return the target state
     */
    public int getTo() {
        return to;
    }

    /**
     * @return the guard
     */
    public Evaluable getGuard() {
        return guard;
    }
}
