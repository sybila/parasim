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
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class Variable implements Expression {

    private final String name;
    private final int index;
    private final VariableValue value;

    public Variable(String name, int index) {
        if (name == null) {
            throw new IllegalArgumentException("The paramater [name] is null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("The paramater [index] has to be a non negative number.");
        }
        this.name = name;
        this.index = index;
        this.value = null;
    }

    private Variable(String name, int index, VariableValue value) {
        if (name == null) {
            throw new IllegalArgumentException("The paramater [name] is null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("The paramater [index] has to be a non negative number.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The paramater [value] is null.");
        }
        this.name = name;
        this.index = index;
        this.value = value;
    }

    public final String getName() {
        return name;
    }

    public final int getIndex() {
        return index;
    }

    @Override
    public final float evaluate(Point point) {
        return value == null ? point.getValue(index) : value.getValue();
    }

    @Override
    public final float evaluate(float[] point) {
        return value == null ? point[index] : value.getValue();
    }

    @Override
    public final Expression substitute(VariableValue... variableValues) {
        int indexBefore = 0;
        for (VariableValue v: variableValues) {
            if (v.getVariable().equals(this)) {
                return new Variable(name, index, v);
            }
            if (v.getVariable().getIndex() < index) {
                indexBefore++;
            }
        }
        if (indexBefore != 0) {
            return new Variable(name, index - indexBefore);
        } else {
            return this;
        }
    }

    @Override
    public Expression substitute(Collection<VariableValue> variableValues) {
        int indexBefore = 0;
        for (VariableValue v: variableValues) {
            if (v.getVariable().equals(this)) {
                return new Variable(name, index, v);
            }
            if (v.getVariable().getIndex() < index) {
                indexBefore++;
            }
        }
        if (indexBefore != 0) {
            return new Variable(name, index - indexBefore);
        } else {
            return this;
        }
    }

    @Override
    public final String toFormula() {
        return value == null ? name : Float.toString(value.getValue());
    }

    @Override
    public String toFormula(VariableRenderer renderer) {
        return value == null ? renderer.render(this) : Float.toString(value.getValue());
    }

}
