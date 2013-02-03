/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.util;

/**
 * Counter of lock and unlock actions. Non-concurrent.
 *
 * Essentially a stack of "lock" actions, which are canceled by "unlock"
 * actions. It is in "accessible state" when the stack is empty.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimpleLock {

    private int lock = 0;

    /**
     * Constructs unlocked counter.
     */
    public SimpleLock() {
    }

    /**
     * Query the counter.
     *
     * @return
     * <code>true</code> when the stack is empty,
     * <code>false</code> otherwise.
     */
    public boolean isAccessible() {
        return lock == 0;
    }

    /**
     * Add "lock" to the top of the stack.
     *
     * @throws IllegalStateException on counter overflow.
     */
    public void lock() {
        if (lock == Integer.MAX_VALUE) {
            throw new IllegalStateException("Integer overflow.");
        }
        lock++;
    }

    /**
     * Remove the top of the stack.
     */
    public void unlock() {
        if (lock != 0) {
            lock--;
        }
    }
}
