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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.trajectory.Distance;
import org.sybila.parasim.model.trajectory.EuclideanMetric;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.PointDistanceMetric;
import org.sybila.parasim.model.verification.stlstar.MultiPoint;
import org.sybila.parasim.model.xml.XMLRepresentable;
import org.sybila.parasim.util.Block;
import org.sybila.parasim.util.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Comprises of an inequality, i.e.: <ul> <li>relational operator</li> <li>left
 * side -- a linear combination of variables,</li> <li>right side -- a
 * constant</li>
 *
 * Validity is determined as the validity of inequality in the given point.
 * Value is determined as difference between right and left side, which is
 * positive when valid and negative when not valid.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class LinearPredicate extends Predicate {

    /**
     * Type of relational operator in the predicate
     */
    public static enum Type implements XMLRepresentable {

        /**
         * Left side is equal to the right side
         */
        EQUALS() {

            @Override
            public boolean isValid(float left, float right) {
                return left == right;
            }

            @Override
            public String toString() {
                return "=";
            }

            @Override
            public float getValue(float left, float right) {
                return 0;
            }
        },
        /**
         * Left side is greater than the right side
         */
        GREATER() {

            @Override
            public boolean isValid(float left, float right) {
                return left > right;
            }

            @Override
            public String toString() {
                return ">";
            }
        },
        /**
         * Left side is lesser than the right side
         */
        LESSER() {

            @Override
            public boolean isValid(float left, float right) {
                return left < right;
            }

            @Override
            public String toString() {
                return "<";
            }
        };

        /**
         * @param left Left side of inequality.
         * @param right Right side of inequality.
         * @return Validity of inequality with given sides and this operator.
         */
        public abstract boolean isValid(float left, float right);

        /**
         * @param left Left side of inequality.
         * @param right Right side of inequality.
         * @return Value of inequality with given sides and this operator.
         */
        public float getValue(float left, float right) {
            /*
             * computes absolute difference between left and right sides and
             * then determines its sign from the point validity
             */
            float value = Math.abs(left - right);
            if (!isValid(left, right)) {
                value = -value;
            }
            return value;
        }

        @Override
        public Element toXML(Document doc) {
            return doc.createElement(name().toLowerCase(Locale.ENGLISH));
        }
    }
    private static final PointDistanceMetric<Distance> EUCLIDEAN_DISTANCE = new EuclideanMetric();
    //
    private Map<Pair<Integer, Integer>, Float> terms;
    private float constant, denominator, unstarDenominator;
    private PointVariableMapping mapping;
    private Type type;
    private int starNum;

    private static Distance getLength(Float[] array) {
        float[] empty = new float[array.length];
        Arrays.fill(empty, 0);
        return EUCLIDEAN_DISTANCE.distance(ArrayUtils.toPrimitive(array), empty);
    }

    private float getLeftSideValue(Point p) {
        float value = 0;
        for (Map.Entry<Pair<Integer, Integer>, Float> term : terms.entrySet()) {
            if (term.getKey().second() == 0) { //only evaluate predicates without stars
                if (term.getKey().first() >= p.getDimension()) {
                    throw new IllegalArgumentException(
                            "The point has too few dimensions to be evaluated.");
                }
                value += p.getValue(term.getKey().first()) * term.getValue();
            }
        }
        return value;
    }

    private float getLeftSideValue(float[] p) {
        float value = 0;
        for (Map.Entry<Pair<Integer, Integer>, Float> term : terms.entrySet()) {
            if (term.getKey().second() == 0) { // only evalueate predicates without stars
                if (term.getKey().first() >= p.length) {
                    throw new IllegalArgumentException(
                            "The point has too few dimensions to be evaluated.");
                }
                value += p[term.getKey().first()] * term.getValue();
            }
        }
        return value;
    }

    private float getLeftSideValue(MultiPoint mp) {
        float value = 0;
        for (Map.Entry<Pair<Integer, Integer>, Float> term : terms.entrySet()) {
            int var = term.getKey().first();
            int freeze = term.getKey().second();
            if (mp.getDimension() <= freeze) {
                throw new IllegalArgumentException("The multipoint has too few points to be evaluated.");
            }
            Point p = mp.getPoint(freeze);
            if (p.getDimension() <= var) {
                throw new IllegalArgumentException("The point has too few dimensions to be evaluated.");
            }
            value += p.getValue(var) * term.getValue();
        }
        return value;
    }

    private static Set<Integer> mapFirst(Set<Pair<Integer, Integer>> pairSet) {
        Set<Integer> result = new HashSet<>();
        for (Pair<Integer, Integer> pair : pairSet) {
            Integer first = pair.first();
            if (first != null) {
                result.add(first);
            }
        }
        return result;
    }

    /**
     * Creates a new linear predicate.
     *
     * @param multipliers List of left-side multipliers, each with associated
     * variable index and frozen time index.
     * @param constant Right-side value.
     * @param type Type of relational operator.
     * @param variables Mapping between variable and indices.
     */
    public LinearPredicate(Map<Pair<Integer, Integer>, Float> multipliers, float constant,
            Type type, PointVariableMapping variables) {
        super(mapFirst(multipliers.keySet()));
        if (multipliers.isEmpty()) {
            throw new IllegalArgumentException(
                    "There has to be at least one variable in the predicate.");
        }

        // compute star number and check there are no nulls in multipliers //
        starNum = 0;
        for (Map.Entry<Pair<Integer, Integer>, Float> term : multipliers.entrySet()) {
            Integer star = term.getKey().second();
            Validate.notNull(term.getKey().first());
            Validate.notNull(star);
            Validate.notNull(term.getValue());
            if (star > starNum) {
                starNum = star;
            }
        }

        // assignments //
        terms = multipliers;
        this.constant = constant;
        mapping = variables;
        this.type = type;

        // compute denominator //
        Block.Builder<Float> multiVectors = new Block.Builder<>(starNum + 1);
        for (Map.Entry<Pair<Integer, Integer>, Float> term : multipliers.entrySet()) {
            multiVectors.getList(term.getKey().second()).add(term.getValue());
        }
        denominator = 0;
        for (int i = 0; i <= starNum; i++) {
            denominator += getLength(multiVectors.getList(i).toArray(new Float[0])).value();
        }
        unstarDenominator = getLength(multiVectors.getList(0).toArray(new Float[0])).value();
    }

    private boolean isValid(float leftSide, float rightSide) {
        return type.isValid(leftSide, rightSide);
    }

    @Override
    public boolean isValid(Point p) {
        return isValid(getLeftSideValue(p), constant);
    }

    @Override
    public float getValue(Point p) {
        return getUnstarValue(getLeftSideValue(p));
    }

    @Override
    public float getValue(float[] point) {
        return getUnstarValue(getLeftSideValue(point));
    }

    @Override
    public float getValue(MultiPoint mp) {
        return type.getValue(getLeftSideValue(mp), constant) / denominator;
    }

    @Override
    public boolean isValid(MultiPoint mp) {
        return type.isValid(getLeftSideValue(mp), constant);
    }

    /**
     * Computes value from left side.
     */
    private float getUnstarValue(float leftSide) {
        return type.getValue(leftSide, constant) / unstarDenominator;
    }

    @Override
    public Element toXML(Document doc) {
        Element predicate = doc.createElement(LinearPredicateFactory.PREDICATE_NAME);
        /*
         * Linear combination
         */
        for (Map.Entry<Pair<Integer, Integer>, Float> term : terms.entrySet()) {
            Element var = doc.createElement(LinearPredicateFactory.VARIABLE_NAME);
            var.appendChild(doc.createTextNode(mapping.getName(term.getKey().first())));
            var.setAttribute(LinearPredicateFactory.MULTIPLIER_ATTRIBUTE, term.getValue().toString());
            var.setAttribute(LinearPredicateFactory.FREEZE_ATTRIBUTE, term.getKey().second().toString());
            predicate.appendChild(var);
        }

        predicate.appendChild(type.toXML(doc));

        Element val = doc.createElement(LinearPredicateFactory.VALUE_NAME);
        val.appendChild(doc.createTextNode(Float.toString(constant)));
        predicate.appendChild(val);

        return predicate;
    }

    @Override
    public int getStarNumber() {
        return starNum;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<Pair<Integer, Integer>, Float> term : terms.entrySet()) {
            if (first) {
                first = false;
            } else if (term.getValue() > 0) {
                result.append("+");
            }
            result.append(term.getValue());
            result.append("*");
            result.append(mapping.getName(term.getKey().first()));
            result.append(term.getKey().second().toString());
        }
        result.append(type.toString());
        result.append(constant);
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj instanceof LinearPredicate)) {
            return false;
        }
        LinearPredicate target = (LinearPredicate) obj;
        if (constant != target.constant) {
            return false;
        }
        if (terms.equals(target.terms)) {
            return false;
        }
        if (!mapping.equals(target.mapping)) {
            return false;
        }
        if (!type.equals(target.type)) {
            return false;
        }
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
