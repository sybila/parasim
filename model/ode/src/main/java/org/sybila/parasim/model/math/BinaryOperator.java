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

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public abstract class BinaryOperator<E extends BinaryOperator> implements Expression<E> {

    private final Expression left;

    private final Expression right;

    public BinaryOperator(Expression left, Expression right) {
        if (left == null) {
            throw new IllegalArgumentException("The paramater [left] is null.");
        }
        if (right == null) {
            throw new IllegalArgumentException("The paramater [right] is null.");
        }
        this.left = left;
        this.right = right;
    }

    public BinaryOperator(Expression... expressions) {
        if (expressions.length < 2) {
            throw new IllegalArgumentException("The number of expression has to be at least 2.");
        }
        if (expressions.length == 2) {
            this.left = expressions[0];
            this.right = expressions[1];
        } else {
            Expression l = create(expressions[0], expressions[1]);
            for (int i=2; i<expressions.length-1; i++) {
                l = create(l, expressions[i]);
            }
            this.left = l;
            this.right = expressions[expressions.length - 1];
        }
    }

    public final Expression getLeft() {
        return left;
    }

    public final Expression getRight() {
        return right;
    }

    @Override
    public <T> T traverse(TraverseFunction<T> function) {
        return function.apply(this, (T) getLeft().traverse(function), (T) getRight().traverse(function));
    }

    abstract protected BinaryOperator create(Expression left, Expression right);

}
