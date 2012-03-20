package org.sybila.parasim.model.verification.stl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents an time interval and enables basic operations with it's bounds.
 * 
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class TimeInterval implements FormulaInterval {
    private float lower, upper;
    private IntervalBoundaryType lowerType, upperType;

    public TimeInterval(float lower, float upper, IntervalBoundaryType lowerType, IntervalBoundaryType upperType) {
        if (lower < 0) {
            throw new IllegalArgumentException("Parameter lower must be >= 0.");
        }
        if (lower >= upper) {
            throw new IllegalArgumentException("Parameter upper must greater or equal to parameter lower.");
        }
        if (lowerType == null) {
            throw new IllegalArgumentException("Parameter lowerType is null.");
        }
        if (upperType == null) {
            throw new IllegalArgumentException("Parameter upperType is null.");
        }
        this.lower = lower;
        this.upper = upper;
        this.lowerType = lowerType;
        this.upperType = upperType;
    }

    public TimeInterval(float lower, float upper, IntervalBoundaryType type) {
        this(lower, upper, type, type);
    }

    @Override
    public float getLowerBound() {
        return lower;
    }

    @Override
    public float getUpperBound() {
        return upper;
    }

    @Override
    public IntervalBoundaryType getLowerBoundaryType() {
        return lowerType;
    }

    @Override
    public IntervalBoundaryType getUpperBoundaryType() {
        return upperType;
    }

    /**
     * Returns the value of the comparison <code>getLowerBound</code> OP <b>a</b>
     * where OP is either "&lt;=" if the left bound is tight and "&lt;" if it is open.
     *
     * @param a Value to compare with lower bound
     * @return Result of comparison <code>getLowerBound</code> OP <b>a</b>.
     */
    public boolean largerThenLower(float a) {
        if (lowerType == IntervalBoundaryType.CLOSED) {
            return lower <= a;
        } else {
            return lower < a;
        }
    }

    /**
     * Returns the value of the comparison <b>a</b> OP <code>getUpperBound</code>
     * where OP is either "&lt;=" if the right bound is tight and "&lt;" if it is open.
     * 
     * @param a Value to compare with upper bound
     * @return Result of comparison <b>a</b> OP <code>getUpperBound</code>.
     */
    public boolean smallerThenUpper(float a) {
        if (upperType == IntervalBoundaryType.CLOSED) {
            return a <= upper;
        } else {
            return a < upper;
        }
    }

    @Override
    public Element toXML(Document doc) {
        Element target = doc.createElement(FormulaIntervalFactory.INTERVAL_NAME);
        target.appendChild(FormulaIntervalFactory.getLowerBound(doc, getLowerBound(), getLowerBoundaryType()));
        target.appendChild(FormulaIntervalFactory.getUpperBound(doc, getUpperBound(), getUpperBoundaryType()));
        return target;
    }
    
    @Override
    public String toString() {
        return (lowerType == IntervalBoundaryType.CLOSED ? "[" : "(") + lower
                + "," + upper + (upperType == IntervalBoundaryType.CLOSED ? "]" : ")");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true; //same instance (convenience)
        }
        if (!(obj instanceof FormulaInterval)) {
            return false; //must implement FormulaInterval
        }
        FormulaInterval target = (FormulaInterval) obj;
        if (!getLowerBoundaryType().equals(target.getLowerBoundaryType())) {
            return false;
        }
        if (!getUpperBoundaryType().equals(target.getUpperBoundaryType())) {
            return false;
        }
        if (Float.floatToIntBits(getLowerBound()) != Float.floatToIntBits(target.getLowerBound())) {
            return false;
        }
        if (Float.floatToIntBits(getUpperBound()) != Float.floatToIntBits(target.getUpperBound())) {
            return false;
        }
        return true; //ruled out
    }

    @Override
    public int hashCode() {
        final int prime = 29;
        int result = getLowerBoundaryType().ordinal();
        result = result * prime + getUpperBoundaryType().ordinal();
        result = result * prime + Float.floatToIntBits(getUpperBound());
        result = result * prime + Float.floatToIntBits(getLowerBound());
        return result;
    }
}