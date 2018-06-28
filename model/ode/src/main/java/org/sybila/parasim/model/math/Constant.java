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

import java.util.Collection;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class Constant implements Expression<Constant> {

    private final float value;

    public Constant(float value) {
        this.value = value;
    }

    @Override
    public float evaluate(Point point) {
        return value;
    }

    @Override
    public float evaluate(float[] point) {
        return value;
    }

    @Override
    public Constant release(Collection<Expression> expressions) {
        return this;
    }

    @Override
    public Constant release(Expression... expressions) {
        return this;
    }

    @Override
    public Constant substitute(SubstitutionValue... substitutionValues) {
        return this;
    }

    @Override
    public Constant substitute(Collection<SubstitutionValue> substitutionValues) {
        return this;
    }

    @Override
    public String toFormula() {
        return toFormula(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder toFormula(StringBuilder builder) {
        return value >= 0 ? builder.append(value) : builder.append("(").append(value).append(")");
    }

    @Override
    public StringBuilder toFormula(StringBuilder builder, VariableRenderer renderer) {
        return toFormula(builder);
    }

    @Override
    public String toFormula(VariableRenderer renderer) {
        return toFormula();
    }

    @Override
    public <T> T traverse(TraverseFunction<T> function) {
        return function.apply(this);
    }

    @Override
    public int getNodeCount() {
        return 1;
    }

    @Override
    public void serialize(byte[] info, float[] constants, int[] variables, position p) {
        info[p.index] = 'C';
        constants[p.index] = value;
        p.index++;
    }

    public float getValue() {
        return value;
    }

}
