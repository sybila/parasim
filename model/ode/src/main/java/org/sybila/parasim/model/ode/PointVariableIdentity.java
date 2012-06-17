package org.sybila.parasim.model.ode;

/**
 * Maps dimensions to their string representation. To be used
 * when there is no better mapping available.
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PointVariableIdentity implements PointVariableMapping {

    private boolean checkInt(Integer input) {
        return !(input < 0);
    }

    @Override
    public int getDimension() {
        throw new UnsupportedOperationException("Dimension of PointVariableIdentity is not specified by default.");
    }

    @Override
    public Integer getKey(String variableName) {
        Integer result;
        try {
            result = new Integer(variableName);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (checkInt(result)) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getName(Integer variableKey) {
        if (checkInt(variableKey)) {
            return variableKey.toString();
        } else {
            return null;
        }
    }
}
