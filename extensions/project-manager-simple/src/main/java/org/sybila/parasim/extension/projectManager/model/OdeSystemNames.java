package org.sybila.parasim.extension.projectManager.model;

import java.util.Set;
import org.sybila.parasim.model.math.Parameter;
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

    public float getValue(String name) {
        OdeSystemVariable var = system.getVariables().get(name);
        if (var != null) {
            return system.getInitialVariableValue(var).getValue();
        }
        Parameter par = system.getAvailableParameters().get(name);
        if (par != null) {
            return system.getDeclaredParamaterValue(par).getValue();
        }
        throw new IllegalArgumentException("Ode system contains no variable or parameter of such value.");
    }
}
