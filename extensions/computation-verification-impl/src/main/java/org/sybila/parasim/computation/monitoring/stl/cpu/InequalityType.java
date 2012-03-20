package org.sybila.parasim.computation.monitoring.stl.cpu;

/**
 * Represents possible types of inequalities to appear in predicates
 * of an STL formulae.
 *
 * Name      Expression   Robustness
 * LESS      x < const    const - x
 * MORE      x > const    x - const
 * //EQUAL     x == const   -abs(x-const)
 * //NOTEQUAL  x != const   abs(x-const)
 *
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public enum InequalityType {
    LESS 
    {
        public float value(float x, float constant)
        {
            return constant - x;
        }
        public float derivative(float d)
        {
            return -d;
        }
        public String toString()
        {
            return "<";
        }
    },
    MORE
    {
        public float value(float x, float constant)
        {
            return x - constant;
        }
        public float derivative(float d)
        {
            return d;
        }
        public String toString()
        {
            return ">";
        }
    }/*,
    EQUAL / not linear
    {
        public float evaluate(float x, float constant)
        {
            return -Math.abs(constant - x);
        }
        public String toString()
        {
            return "==";
        }
    },
    NOTEQUAL / not linear
    {
        public float evaluate(float x, float constant)
        {
            return Math.abs(constant - x);
        }
        public String toString()
        {
            return "!=";
        }
    }*/;

    /**
     * Gives robustness of the expression for given values.
     * @param x value compared to constant
     * @param constant comparing values
     * @return Distance of expression from being satisfied or violated.
     */
    abstract public float value(float x, float constant);

    /**
     * Returns how the value of the expression will change if x changes with
     * the given rate d.
     * @param d Rate of change of variable x.
     * @return Rate of change of expression.
     */
    abstract public float derivative(float d);

    @Override
    abstract public String toString();
}
