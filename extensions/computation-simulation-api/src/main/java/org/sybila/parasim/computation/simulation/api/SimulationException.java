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
package org.sybila.parasim.computation.simulation.api;

/**
 * Thrown when a simulations crashes.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class SimulationException extends Exception {

    /**
     * Constructs a <tt>SimulationException</tt> with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     *
     * @param cause the cause (which is saved for later retrieval by the
     *      {@link Throwable#getCause()} method).  (A <tt>null</tt> value is
     *      permitted, and indicates that the cause is nonexistent or
     *      unknown.)
     */
    public SimulationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a <tt>SimulationException</tt> with the specified detail message and
     * cause.
     *
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param message the String that contains a detailed message
     * @param cause the cause (which is saved for later retrieval by the
     *      {@link Throwable#getCause()} method).  (A <tt>null</tt> value
     *      is permitted, and indicates that the cause is nonexistent or
     *      unknown.)
     */
    public SimulationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a <tt>SimulationException</tt> with the specified detail message.
     * A detail message is a String that describes this particular exception.
     *
     * @param message the String that contains a detailed message
     */
    public SimulationException(String message) {
        super(message);
    }

    /**
     * Constructs a <tt>SimulationException</tt> with no detail message.
     */
    public SimulationException() {
    }
}
