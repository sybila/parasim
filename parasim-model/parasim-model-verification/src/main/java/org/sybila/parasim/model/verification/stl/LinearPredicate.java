package org.sybila.parasim.model.verification.stl;

import java.util.Arrays;
import java.util.Locale;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.variables.PointVariableMapping;
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
public abstract class LinearPredicate extends Predicate {
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
            return doc.createElement(this.toString()
                    .toLowerCase(Locale.ENGLISH));
        }
    }

    private float[] terms;
    private float constant;
    private PointVariableMapping mapping;
    private Type type;

    private float getLeftSideValue(Point p) {
        if (p.getDimension() < terms.length) {
            throw new IllegalArgumentException(
                    "Point has too few values for the term to be evaluated.");
        }
        float value = 0;
        for (int index = 0; index < p.getDimension(); index++) {
            value += terms[index] * p.getValue(index);
        }
        return value;
    }

    public LinearPredicate(float[] terms, float constant, Type type,
            PointVariableMapping variables) {
        terms = Arrays.copyOf(terms, terms.length);
        this.constant = constant;
        mapping = variables;
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
        Element predicate = doc.createElement(LinearPredicateFactory.PREDICATE_NAME);
        /* Linear combination */
        for (int index = 0; index < terms.length; index++) {
            Element var = doc.createElement(LinearPredicateFactory.VARIABLE_NAME);
            var.appendChild(doc.createTextNode(mapping.getName(new Integer(index))));
            var.setAttribute(LinearPredicateFactory.MULTIPLIER_ATTRIBUTE, Float.toString(terms[index]));
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
        int index = 0;
        while ((index < terms.length) && (terms[index] != 0)) {
            index++;
        }
        if (index == terms.length) {
            result.append("0");
        } else {
            result.append(terms[index]);
            result.append("*");
            result.append(mapping.getName(new Integer(index)));
            while (index < terms.length) {
                if (terms[index] != 0) {
                    if (terms[index] > 0) {
                        result.append("+");
                    }
                    result.append(terms[index]);
                    result.append("*");
                    result.append(mapping.getName(new Integer(index)));
                }
            }
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
        if (!Arrays.equals(terms, target.terms))
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
        for (int index = 0; index < terms.length; index++) {
            result = prime * result + Float.floatToIntBits(terms[index]);
        }
        result = prime * result + type.hashCode();
        return result;
    }

}
