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
package org.sybila.parasim.model.math;

import java.io.Serializable;
import java.util.Collection;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface Expression<E extends Expression> extends Serializable {

    class position {
        public int index;
    }

    float evaluate(Point point);

    float evaluate(float[] point);

    E release(Expression... expressions);

    E release(Collection<Expression> expressions);

    E substitute(SubstitutionValue... substitutionValues);

    E substitute(Collection<SubstitutionValue> substitutionValues);

    <T> T traverse(TraverseFunction<T> function);

    String toFormula();

    String toFormula(VariableRenderer renderer);

    StringBuilder toFormula(StringBuilder builder);

    StringBuilder toFormula(StringBuilder builder, VariableRenderer renderer);

    int getNodeCount();

    void serialize(byte[] info, float[] constants, int[] variables, position p);

    public static interface TraverseFunction<T> {

        T apply(Expression expression, T... subs);

    }

}
