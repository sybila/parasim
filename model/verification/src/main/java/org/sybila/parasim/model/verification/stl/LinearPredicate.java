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
package org.sybila.parasim.model.verification.stl;

import java.util.Locale;
import java.util.Map;

import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.xml.XMLRepresentable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Comprises of an inequality, i.e.:
 * <ul>
 * <li>relational operator</li>
 * <li>left side -- a linear combination of variables,</li>
 * <li>right side -- a constant</li>
 *
 * Validity is determined as the validity of inequality in the given point.
 * Value is determined as difference between right and left side, which is
 * positive when valid and negative when not valid.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class LinearPredicate extends Predicate {
    /** Type of relational operator in the predicate */
    public static enum Type implements XMLRepresentable {
        /** Left side is equal to the right side */
        EQUALS,
        /** Left side is greater than the right side */
        GREATER,
        /** Left side is lesser than the right side */
        LESSER;

        /**
         * @param left
         *            Left side of inequality.
         * @param right
         *            Right side of inequality.
         * @return Validity of inequality with given sides and this operator.
         */
        public boolean isValid(float left, float right) {
            switch (this) {
            case EQUALS:
                return left == right;
            case GREATER:
                return left > right;
            case LESSER:
                return left < right;
            default:
                assert false;
                return false;
            }
        }

        @Override
        public String toString() {
            switch (this) {
            case EQUALS:
                return "=";
            case GREATER:
                return ">";
            case LESSER:
                return "<";
            default:
                assert false;
                return null;
            }
        }

        @Override
        public Element toXML(Document doc) {
            return doc.createElement(name().toLowerCase(Locale.ENGLISH));
        }
    }

    private Map<Integer, Float> terms;
    private float constant;
    private PointVariableMapping mapping;
    private Type type;

    private float getLeftSideValue(Point p) {
        float value = 0;
        for (Map.Entry<Integer, Float> term : terms.entrySet()) {
            if (term.getKey() >= p.getDimension()) {
                throw new IllegalArgumentException(
                        "The point has too few dimensions to be evaluated.");
            }
            value += p.getValue(term.getKey()) * term.getValue();
        }
        return value;
    }

    /**
     * Creates a new linear predicate.
     *
     * @param multipliers
     *            List of left-side multipliers, each with associated variable
     *            index.
     * @param constant
     *            Right-side value.
     * @param type
     *            Type of relational operator.
     * @param variables
     *            Mapping between variable and indices.
     */
    public LinearPredicate(Map<Integer, Float> multipliers, float constant,
            Type type, PointVariableMapping variables) {
        if (multipliers.isEmpty()) {
            throw new IllegalArgumentException(
                    "There has to be at least one variable in the predicate.");
        }
        terms = multipliers;
        this.constant = constant;
        mapping = variables;
        this.type = type;
    }

    private boolean isValid(float leftSide, float rightSide) {
        return type.isValid(leftSide, rightSide);
    }

    @Override
    public boolean getValidity(Point p) {
        return isValid(getLeftSideValue(p), constant);
    }

    @Override
    public float getValue(Point p) {
        /*
         * computes absolute difference between left and right sides and then
         * determines its sign from the point validity
         */
        float leftSide = getLeftSideValue(p);
        float diff = Math.abs(leftSide - constant);
        if (isValid(leftSide, constant)) {
            diff = -diff;
        }
        return diff;
    }

    @Override
    public Element toXML(Document doc) {
        Element predicate = doc
                .createElement(LinearPredicateFactory.PREDICATE_NAME);
        /* Linear combination */
        for (Map.Entry<Integer, Float> term : terms.entrySet()) {
            Element var = doc
                    .createElement(LinearPredicateFactory.VARIABLE_NAME);
            var.appendChild(doc.createTextNode(mapping.getName(term.getKey())));
            var.setAttribute(LinearPredicateFactory.MULTIPLIER_ATTRIBUTE, term
                    .getValue().toString());
            predicate.appendChild(var);
        }

        predicate.appendChild(type.toXML(doc));

        Element val = doc.createElement(LinearPredicateFactory.VALUE_NAME);
        val.appendChild(doc.createTextNode(Float.toString(constant)));
        predicate.appendChild(val);

        return predicate;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<Integer, Float> term : terms.entrySet()) {
            if (first) {
                first = false;
            } else if (term.getValue() > 0) {
                result.append("+");
            }
            result.append(term.getValue());
            result.append("*");
            result.append(mapping.getName(term.getKey()));
        }
        result.append(type.toString());
        result.append(constant);
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if ((obj instanceof LinearPredicate))
            return false;
        LinearPredicate target = (LinearPredicate) obj;
        if (constant != target.constant)
            return false;
        if (terms.equals(target.terms))
            return false;
        if (!mapping.equals(target.mapping))
            return false;
        if (!type.equals(target.type))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 29;
        int result = mapping.hashCode();
        result = prime * result + Float.floatToIntBits(constant);
        result = prime * result + terms.hashCode();
        result = prime * result + type.hashCode();
        return result;
    }

}
