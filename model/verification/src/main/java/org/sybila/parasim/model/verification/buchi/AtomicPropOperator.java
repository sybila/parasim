package org.sybila.parasim.model.verification.buchi;

/**
 * Operators in atomic propositions of transition guards.
 * @author Sven Dražan <sven@mail.muni.cz>
 */
public enum AtomicPropOperator {
    AP_LESS
    {
        float evaluate(float x, float y)
        {
            return y-x;
        }

        boolean validate(float x, float y)
        {
            return x < y;
        }

        @Override
        public String toString()
        {
            return "<";
        }
    },
    AP_GREATER 
    { 
        float evaluate(float x, float y)
        { 
            return x-y; 
        }

        boolean validate(float x, float y)
        {
            return x > y;
        }
        
        @Override
        public String toString()
        {
            return ">";
        }
    };

    abstract float evaluate(float x, float y);

    abstract boolean validate(float x, float y);

    @Override
    abstract public String toString();
}
