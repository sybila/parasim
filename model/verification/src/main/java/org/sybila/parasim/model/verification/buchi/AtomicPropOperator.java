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

/**
 * Operators in atomic propositions of transition guards.
 * @author Sven Drazan <sven@mail.muni.cz>
 */
public enum AtomicPropOperator {

    AP_LESS {

        float evaluate(float x, float y) {
            return y - x;
        }

        boolean validate(float x, float y) {
            return x < y;
        }

        @Override
        public String toString() {
            return "<";
        }
    },
    AP_GREATER {

        float evaluate(float x, float y) {
            return x - y;
        }

        boolean validate(float x, float y) {
            return x > y;
        }

        @Override
        public String toString() {
            return ">";
        }
    };

    abstract float evaluate(float x, float y);

    abstract boolean validate(float x, float y);

    @Override
    abstract public String toString();
}
