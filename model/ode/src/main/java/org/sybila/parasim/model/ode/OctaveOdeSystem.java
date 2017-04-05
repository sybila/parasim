/**
 * Copyright 2011-2016, Sybila, Systems Biology Laboratory and individual
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.sbml.jsbml.Model;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.math.VariableRenderer;
import org.sybila.parasim.model.math.VariableValue;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OctaveOdeSystem implements OdeSystem {

    private final OdeSystem odeSystem;
    private String octaveString;

    private static final VariableRenderer VARIABLE_RENDERER = new VariableRenderer() {

        @Override
        public String render(Variable variable) {
            return "x(" + (variable.getIndex() + 1) + ")";
        }
    };

    public OctaveOdeSystem(OdeSystem odeSystem) {
        if (odeSystem == null) {
            throw new IllegalArgumentException("The parameter [odeSystem] is null.");
        }
        this.odeSystem = odeSystem;
    }

    @Override
    public Model getOriginalModel() {
        return odeSystem.getOriginalModel();
    }

    @Override
    public int dimension() {
        return odeSystem.dimension();
    }

    @Override
    public Map<String, Parameter> getAvailableParameters() {
        return odeSystem.getAvailableParameters();
    }

    @Override
    public ParameterValue getDeclaredParamaterValue(Parameter parameter) {
        return odeSystem.getDeclaredParamaterValue(parameter);
    }

    @Override
    public VariableValue getInitialVariableValue(Variable variable) {
        return odeSystem.getInitialVariableValue(variable);
    }

    @Override
    public Parameter getParameter(int dimension) {
        return odeSystem.getParameter(dimension);
    }

    @Override
    public OdeSystemVariable getVariable(int dimension) {
        return odeSystem.getVariable(dimension);
    }

    @Override
    public Map<String, OdeSystemVariable> getVariables() {
        return odeSystem.getVariables();
    }

    @Override
    public boolean isParamater(int dimension) {
        return odeSystem.isParamater(dimension);
    }

    @Override
    public boolean isVariable(int dimension) {
        return odeSystem.isVariable(dimension);
    }

    public String octaveName() {
        return "f";
    }

    public String octaveString(boolean timeFirst) {
        if (octaveString == null) {
            StringBuilder builder = new StringBuilder();
            if (timeFirst) {
                builder.append("function xdot = f(t, x) ");
            } else {
                builder.append("function xdot = f(x, t) ");
            }
            builder.append("xdot = zeros(").append(dimension()).append(", 1);");
            for (OdeSystemVariable variable: odeSystem) {
                builder.append("xdot(").append(variable.getIndex() + 1).append(") = ").append(variable.getRightSideExpression().toFormula(VARIABLE_RENDERER)).append("; ");
            }
            octaveString = builder.append("endfunction;").toString();
        }
        return octaveString;
    }

    @Override
    public Iterator<OdeSystemVariable> iterator() {
        return odeSystem.iterator();
    }

    @Override
    public OdeSystem release(Expression... expressions) {
        return odeSystem.release(expressions);
    }

    @Override
    public OdeSystem release(Collection<Expression> expressions) {
        return odeSystem.release(expressions);
    }

    @Override
    public OdeSystem substitute(ParameterValue... parameterValues) {
        return new OctaveOdeSystem(odeSystem.substitute(parameterValues));
    }

    @Override
    public OdeSystem substitute(Collection<ParameterValue> parameterValues) {
        return new OctaveOdeSystem(odeSystem.substitute(parameterValues));
    }

}
