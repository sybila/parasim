package org.sybila.parasim.model.verification.buchi;

/**
 * Represents the conjunction or disjuction inside a group of atomic propositions.
 * @author <a href="mailto:sven@mail.muni.cz">Sven Dražan</a>
 */
public enum EvaluableGroupType {
    AP_GROUP_AND
    {
        @Override
        public String toString()
        {
            return "&&";
        }
    },
    AP_GROUP_OR
    {
        @Override
        public String toString()
        {
            return "||";
        }
    };    

    @Override
    abstract public String toString();
}
