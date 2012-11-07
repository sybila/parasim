package org.sybila.parasim.extension.projectmanager.model;

import java.util.Set;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.VariableValue;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class OdeSystemNames {

    private final OdeSystem system;

    public OdeSystemNames(OdeSystem system) {
        if (system == null) {
            throw new IllegalArgumentException("Argument (system) is null.");
        }
        this.system = system;
    }

    public Set<String> getVariables() {
        return system.getVariables().keySet();
    }

    public Set<String> getParameters() {
        return system.getAvailableParameters().keySet();
    }

    public boolean isVariable(String name) {
        return system.getVariables().containsKey(name);
    }

    public boolean isParameter(String name) {
        return system.getAvailableParameters().containsKey(name);
    }

    public int getDimension(String variableName) {
        OdeSystemVariable var = system.getVariables().get(variableName);
        if (var == null) {
            throw new IllegalArgumentException("System contains no variable `" + variableName + "'.");
        }
        return var.getIndex();
    }

    public Float getValue(String name) {
        OdeSystemVariable var = system.getVariables().get(name);
        if (var != null) {
            VariableValue value = system.getInitialVariableValue(var);
            if (value != null) {
                return value.getValue();
            } else {
                return null;
            }
        }
        Parameter par = system.getAvailableParameters().get(name);
        if (par != null) {
            return system.getDeclaredParamaterValue(par).getValue();
        }
        throw new IllegalArgumentException("Ode system contains no variable or parameter of such value.");
    }

    public String getVariable(int dimension) {
        return system.getVariable(dimension).getName();
    }

    public String getName(int dimension) {
        if (dimension < 0 || dimension >= system.dimension()) {
            throw new IndexOutOfBoundsException("Dimension out of bounds: " + dimension);
        }
        if (system.isParamater(dimension)) {
            return system.getParameter(dimension).getName();
        } else if (system.isVariable(dimension)) {
            return system.getVariable(dimension).getName();
        } else {
            throw new IllegalStateException("Dimension that is not out of bounds, and is neither a parameter or a variable.");
        }
    }

    public boolean isSubstituted(String paramName) {
        Parameter param = system.getAvailableParameters().get(paramName);
        if (param == null) {
            throw new IllegalArgumentException("System contains no such parameter.");
        }
        return param.isSubstituted();
    }
;
}
