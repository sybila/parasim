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
package org.sybila.parasim.model.math;

import java.util.Collection;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public final class Constant implements Expression {

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
    public Expression substitute(VariableValue... constantValues) {
        return this;
    }

    @Override
    public Expression substitute(Collection<VariableValue> variableValues) {
        return this;
    }

    @Override
    public String toFormula() {
        return Float.toString(value);
    }

    @Override
    public String toFormula(VariableRenderer renderer) {
        return toFormula();
    }

}