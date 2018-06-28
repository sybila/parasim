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

    @Override
    public String toFormula() {
        return toFormula(new StringBuilder()).toString();
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
        if ((getLeft() instanceof BinaryOperator && ((BinaryOperator) getLeft()).getPriority() > getPriority()) || getLeft().getClass().equals(getClass()) || !(getLeft() instanceof BinaryOperator)) {
            getLeft().toFormula(builder, renderer);
        } else {
            builder.append("(");
            getLeft().toFormula(builder, renderer).append(")");
        }
        builder.append(" ").append(getSymbol()).append(" ");
        if ((getRight() instanceof BinaryOperator && ((BinaryOperator) getRight()).getPriority() > getPriority()) || getRight().getClass().equals(getClass()) || !(getRight() instanceof BinaryOperator)) {
            getRight().toFormula(builder, renderer);
        } else {
            builder.append("(");
            getRight().toFormula(builder, renderer).append(")");
        }
        return builder;
    }

    @Override
    public int getNodeCount() {
        return (1 + (left.getNodeCount() + right.getNodeCount()));
    }

    abstract protected BinaryOperator create(Expression left, Expression right);

    abstract protected int getPriority();

    abstract protected String getSymbol();
}
