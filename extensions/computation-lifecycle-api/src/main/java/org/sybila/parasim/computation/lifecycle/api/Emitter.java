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
package org.sybila.parasim.computation.lifecycle.api;

/**
 * The emitter can be used to fork your computation. The contract is
 * a new computation is emitted before the old computation is finished.
 *
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Emitter  {

    /**
     * Emit a new computation. The computation is passed to the executor according
     * to its policy. The contract is a new computation is emitted before the
     * old computation is finished.
     */
    void emit(Computation computation);

    /**
     * Balances the given computation. The computation is passed to the executor
     * according to its policy. The difference from {@link #emit(org.sybila.parasim.computation.lifecycle.api.Computation) }
     * is that the given computation is already emitted.
     */
    void balance(Computation computation);

}
