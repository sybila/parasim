/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.verification.stl;

import java.util.Collection;
import java.util.Set;
import org.sybila.parasim.model.verification.Signal;

/**
 * A general predicate evaluable in a Point of a Trajectory.
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Drazan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public abstract class Predicate extends AbstractFormula implements Signal {

    public Predicate(Collection<Integer> variableIndexes) {
        super(variableIndexes);
    }

    /**
     * Returns the number of used frozen times.
     *
     * @return number of frozen times.
     */
    public abstract int getStarNumber();

    /**
     * Returns set containing frozen time indices which are used in this
     * predicate.
     *
     * @return Frozen time indices.
     */
    public abstract Set<Integer> getStars();

    /**
     * Creates a new predicate with given frozen time dimensions merged into
     * normal time dimension.
     *
     * @param frozen Set of frozen time dimensions to be merged.
     * @return Predicate.
     */
    public abstract Predicate mergeFrozenDimensions(Set<Integer> frozen);

    @Override
    public int getArity() {
        return 0;
    }

    @Override
    public Formula getSubformula(int index) {
        throw new IllegalArgumentException("Predicate has no subformulas.");
    }

    @Override
    public float getTimeNeeded() {
        return 0;
    }

    @Override
    public abstract String toString();

    @Override
    public FormulaType getType() {
        return FormulaType.PREDICATE;
    }
}
