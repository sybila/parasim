/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.math.VariableValue;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public interface OdeSystem extends Iterable<OdeSystemVariable>, Serializable {

    /**
     * @return dimension of the ODE system including parameters.
     */
    int dimension();

    /**
     * @return all available parameters including substituted parameters.
     */
    Map<String, Parameter> getAvailableParameters();

    /**
     * @param variable
     * @return initial value of the given variable.
     * @throws IllegalArgumentException if the variable is null.
     */
    VariableValue getInitialVariableValue(Variable variable);

    /**
     * @param parameter
     * @return declared value of the given parameter
     * @throws IllegalArgumentException if the parameter is null.
     */
    ParameterValue getDeclaredParamaterValue(Parameter parameter);

    /**
     * @param dimension
     * @return parameter on the given dimension
     * @throws IndexOutOfBoundsException if the dimension is out of range
     * @throws IllegalArgumentException if there is no paramater on the given dimension
     */
    Parameter getParameter(int dimension);

    /**
     * @param dimension
     * @return variable on the given dimension
     * @throws IndexOutOfBoundsException if the dimension is out of range
     * @throws IllegalArgumentException if there is no variable on the given dimension
     */
    OdeSystemVariable getVariable(int dimension);

    Map<String, OdeSystemVariable> getVariables();

    /**
     * @param dimension
     * @return true if the dimension contains a parameter, false otherwise
     * @throws IndexOutOfBoundsException if the dimension is out of range
     */
    boolean isParamater(int dimension);

    /**
     * @param dimension
     * @return true if the dimension contains a variable, false otherwise
     * @throws IndexOutOfBoundsException if the dimension is out of range
     */
    boolean isVariable(int dimension);

    /**
     * @param expressions
     * @return releases all right sides of the ODE system
     */
    OdeSystem release(Expression... expressions);

    /**
     * @param expressions
     * @return releases all right sides of the ODE system
     */
    OdeSystem release(Collection<Expression> expressions);

    /**
     * @param expressions
     * @return substitutes all right sides of the ODE system
     */
    OdeSystem substitute(ParameterValue... parameterValues);

    /**
     * @param expressions
     * @return substitutes all right sides of the ODE system
     */
    OdeSystem substitute(Collection<ParameterValue> parameterValues);

}
