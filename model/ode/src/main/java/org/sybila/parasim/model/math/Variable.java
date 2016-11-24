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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class Variable implements Expression<Variable>, Indexable {

    private final String name;
    private final int index;
    private final int originalIndex;
    private final SubstitutionValue<Variable> substitution;

    public Variable(String name, int index) {
        if (name == null) {
            throw new IllegalArgumentException("The paramater [name] is null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("The paramater [index] has to be a non negative number.");
        }
        this.name = name;
        this.index = index;
        this.originalIndex = index;
        this.substitution = null;
    }

    private Variable(String name, int index, int originalIndex) {
        if (name == null) {
            throw new IllegalArgumentException("The paramater [name] is null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("The paramater [index] has to be a non negative number.");
        }
        if (originalIndex < 0) {
            throw new IllegalArgumentException("The paramater [originalIndex] has to be a non negative number.");
        }
        this.name = name;
        this.index = index;
        this.originalIndex = originalIndex;
        this.substitution = null;
    }

    private Variable(String name, int index, int originalIndex, SubstitutionValue<Variable> value) {
        if (name == null) {
            throw new IllegalArgumentException("The paramater [name] is null.");
        }
        if (index < 0) {
            throw new IllegalArgumentException("The paramater [index] has to be a non negative number.");
        }
        if (originalIndex < 0) {
            throw new IllegalArgumentException("The paramater [originalIndex] has to be a non negative number.");
        }
        if (value == null) {
            throw new IllegalArgumentException("The paramater [value] is null.");
        }
        this.name = name;
        this.index = index;
        this.originalIndex = originalIndex;
        this.substitution = value;
    }

    public final String getName() {
        return name;
    }

    @Override
    public final int getIndex() {
        return index;
    }

    @Override
    public int getOriginalIndex() {
        return originalIndex;
    }

    @Override
    public final float evaluate(Point point) {
        return substitution == null ? point.getValue(index) : substitution.getSubstitution().evaluate(point);
    }

    @Override
    public final float evaluate(float[] point) {
        return substitution == null ? point[index] : substitution.getSubstitution().evaluate(point);
    }

    public boolean isSubstituted() {
        return substitution != null;
    }

    @Override
    public Variable release(Expression... expressions) {
        int indexBefore = 0;
        boolean release = false;
        for (Expression e: expressions) {
            if (e instanceof Indexable && ((Indexable)e).getOriginalIndex() < originalIndex) {
                indexBefore++;
            }
            if (e.equals(this)) {
                release = true;
            }
        }
        if ((release && isSubstituted()) || indexBefore != 0) {
            return new Variable(name, index+indexBefore, originalIndex);
        } else {
            return this;
        }
    }

    @Override
    public Variable release(Collection<Expression> expressions) {
        int indexBefore = 0;
        boolean release = false;
        for (Expression e: expressions) {
            if (e instanceof Indexable && ((Indexable)e).getOriginalIndex() < originalIndex) {
                indexBefore++;
            }
            if (e.equals(this)) {
                release = true;
            }
        }
        if ((release && isSubstituted()) || indexBefore != 0) {
            return new Variable(name, index+indexBefore, originalIndex);
        } else {
            return this;
        }
    }

    @Override
    public Variable substitute(SubstitutionValue... substitutionValues) {
        return substitute(Arrays.asList(substitutionValues));
    }

    @Override
    public Variable substitute(Collection<SubstitutionValue> substitutionValues) {
        int indexBefore = 0;
        for (SubstitutionValue v: substitutionValues) {
            if (!(v.getExpression() instanceof Variable)) {
                if (v.getExpression() instanceof Indexable && ((Indexable) v.getExpression()).getIndex() < index) {
                    indexBefore++;
                }
                continue;
            }
            SubstitutionValue<Variable> varValue = (SubstitutionValue<Variable>) v;
            if (varValue.getExpression().equals(this)) {
                return new Variable(name, index, originalIndex, varValue);
            }
            if (varValue.getExpression().getIndex() < index) {
                indexBefore++;
            }
        }
        if (indexBefore != 0) {
            return new Variable(name, index - indexBefore, originalIndex);
        } else {
            return this;
        }
    }

    @Override
    public final String toFormula() {
        return toFormula(VariableRenderer.NAME);
    }

    @Override
    public String toFormula(VariableRenderer renderer) {
        return toFormula(new StringBuilder(), renderer).toString();
    }

    @Override
    public StringBuilder toFormula(StringBuilder builder) {
        return toFormula(builder, VariableRenderer.NAME);
    }

    @Override
    public StringBuilder toFormula(StringBuilder builder, VariableRenderer renderer) {
        if (substitution == null) {
            return builder.append(renderer.render(this));
        } else {
            return substitution.getSubstitution().toFormula(builder);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Variable other = (Variable) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public <T> T traverse(TraverseFunction<T> function) {
        return function.apply(this);
    }
}
