/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.model.ode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.SubstitutionValue;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.math.VariableValue;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class SimpleOdeSystem implements OdeSystem {

    private final Map<Integer, OdeSystemVariable> variables = new HashMap<>();
    private final Map<Integer, Parameter> parameters = new HashMap<>();
    private final Map<String, Parameter> parametersByName = new HashMap<>();
    private final Map<Variable, VariableValue> initialVariableValues = new HashMap<>();
    private final Map<Parameter, ParameterValue> parameterValues = new HashMap<>();

    public SimpleOdeSystem(Collection<OdeSystemVariable> variables, Collection<VariableValue> initialVariableValues, Collection<ParameterValue> parameterValues) {
        if (variables == null) {
            throw new IllegalArgumentException("The parameter [variables] is null.");
        }
        if (initialVariableValues == null) {
            throw new IllegalArgumentException("The parameter [initialVariableValues] is null.");
        }
        if (parameterValues == null) {
            throw new IllegalArgumentException("The parameter [parameterValues] is null.");
        }
        for(OdeSystemVariable var: variables) {
            this.variables.put(var.getIndex(), var);
        }
        for (VariableValue value: initialVariableValues) {
            this.initialVariableValues.put(value.getExpression(), value);
        }
        for (ParameterValue value: parameterValues) {
            this.parameterValues.put(value.getExpression(), value);
        }
        reloadParameters();
    }

    @Override
    public int dimension() {
        return parameters.size() + variables.size();
    }

    @Override
    public Map<String, Parameter> getAvailableParameters() {
        return Collections.unmodifiableMap(parametersByName);
    }

    @Override
    public ParameterValue getDeclaredParamaterValue(Parameter parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("The parameter [parameter] is null.");
        }
        return parameterValues.get(parameter);
    }

    @Override
    public VariableValue getInitialVariableValue(Variable variable) {
        if (variable == null) {
            throw new IllegalArgumentException("The parameter [variable] is null.");
        }
        return initialVariableValues.get(variable);
    }

    @Override
    public Parameter getParameter(int dimension) {
        checkDimension(dimension);
        return parameters.get(dimension);
    }

    @Override
    public OdeSystemVariable getVariable(int dimension) {
        checkDimension(dimension);
        return variables.get(dimension);
    }

    @Override
    public Collection<OdeSystemVariable> getVariables() {
        return Collections.unmodifiableCollection(variables.values());
    }

    @Override
    public boolean isParamater(int dimension) {
        checkDimension(dimension);
        return parameters.containsKey(dimension);
    }

    @Override
    public boolean isVariable(int dimension) {
        checkDimension(dimension);
        return variables.containsKey(dimension);
    }

    @Override
    public OdeSystem release(Expression... expressions) {
        List<OdeSystemVariable> newVars = new ArrayList<>();
        for (OdeSystemVariable var: variables.values()) {
            newVars.add(new OdeSystemVariable(var.getName(), var.release(expressions).getIndex(), var.getRightSideExpression().release(expressions)));
        }
        return new SimpleOdeSystem(newVars, initialVariableValues.values(), parameterValues.values());
    }

    @Override
    public OdeSystem release(Collection<Expression> expressions) {
        List<OdeSystemVariable> newVars = new ArrayList<>();
        for (OdeSystemVariable var: variables.values()) {
            newVars.add(new OdeSystemVariable(var.getName(), var.release(expressions).getIndex(), var.getRightSideExpression().release(expressions)));
        }
        return new SimpleOdeSystem(newVars, initialVariableValues.values(), parameterValues.values());
    }

    @Override
    public OdeSystem substitute(ParameterValue... parameterValues) {
        List<OdeSystemVariable> newVars = new ArrayList<>();
        for (OdeSystemVariable var: variables.values()) {
            newVars.add(new OdeSystemVariable(var.getName(), var.substitute(parameterValues).getIndex(), var.getRightSideExpression().substitute(parameterValues)));
        }
        return new SimpleOdeSystem(newVars, initialVariableValues.values(), this.parameterValues.values());
    }

    @Override
    public OdeSystem substitute(Collection<ParameterValue> parameterValues) {
        Collection<SubstitutionValue> substitution = new ArrayList<>();
        substitution.addAll(parameterValues);
        List<OdeSystemVariable> newVars = new ArrayList<>();
        for (OdeSystemVariable var: variables.values()) {
            newVars.add(new OdeSystemVariable(var.getName(), var.substitute(substitution).getIndex(), var.getRightSideExpression().substitute(substitution)));
        }
        return new SimpleOdeSystem(newVars, initialVariableValues.values(), this.parameterValues.values());
    }

    @Override
    public Iterator<OdeSystemVariable> iterator() {
        return variables.values().iterator();
    }

    private void reloadParameters() {
        parameters.clear();
        for (OdeSystemVariable var: variables.values()) {
            var.getRightSideExpression().traverse(new Expression.TraverseFunction<Object>() {
                @Override
                public Object apply(Expression expression, Object... subs) {
                    if (expression instanceof Parameter) {
                        if (!((Parameter) expression).isSubstituted()) {
                            parameters.put(((Parameter) expression).getIndex(), (Parameter) expression);
                        }
                        parametersByName.put(((Parameter) expression).getName(), (Parameter) expression);
                    }
                    return null;
                }
            });
        }
    }

    private void checkDimension(int dim) {
        if (dim < 0 || dim >= dimension()) {
            throw new IndexOutOfBoundsException("The dimension ["+dim+"] of [0, " + (dimension()-1) + "].");
        }
    }
}
