package org.sybila.parasim.model.ode;

/**
 * {@link PointVariableMapping} which is constructed from an {@link OdeSystem}.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 *
 */
public class OdeVariableMapping implements PointVariableMapping {
    private int dimension;
    private DoubleMap<Integer> mapping = new DoubleMap<Integer>();

    /**
     * @param source System from which the variable names are derived.
     * @throws IllegalArgumentException when there is duplicate variable name.
     */
    public OdeVariableMapping(OdeSystem source) {
        dimension = source.dimension();
        for (int index = 0; index < dimension; index++) {
            Variable var = source.getVariable(index);
            if (!mapping.put(index, var.getName())) {
                mapping.clear();
                throw new IllegalArgumentException("Duplicate variable name.");
            }
        }
    }

    @Override
    public int getDimension() {
        return dimension;
    }

    @Override
    public String getName(Integer variableKey) {
        return mapping.getName(variableKey);
    }

    @Override
    public Integer getKey(String variableName) {
        return mapping.getKey(variableName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OdeVariableMapping)) return false;
        OdeVariableMapping target = (OdeVariableMapping) obj;
        if (dimension != target.dimension) return false;
        return mapping.equals(target.mapping);
    }

    @Override
    public int hashCode() {
        final int prime = 53;
        return prime*mapping.hashCode()+dimension;
    }
}
