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
package org.sybila.parasim.model.sbml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.math.Divide;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Minus;
import org.sybila.parasim.model.math.Negation;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.Plus;
import org.sybila.parasim.model.math.Power;
import org.sybila.parasim.model.math.SubstitutionValue;
import org.sybila.parasim.model.math.Times;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.math.VariableValue;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;
import org.sybila.parasim.model.ode.SimpleOdeSystem;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class SBMLOdeSystem implements OdeSystem {

    private final OdeSystem odeSystem;

    private static final Logger LOGGER = LoggerFactory.getLogger(SBMLOdeSystem.class);

    public SBMLOdeSystem(Model model) {
        this.odeSystem = setup(model);
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

    @Override
    public Iterator<OdeSystemVariable> iterator() {
        return odeSystem.iterator();
    }

    @Override
    public OdeSystem release(Collection<Expression> expressions) {
        return odeSystem.release(expressions);
    }

    @Override
    public OdeSystem release(Expression... expressions) {
        return odeSystem.release(expressions);
    }

    @Override
    public OdeSystem substitute(Collection<ParameterValue> parameterValues) {
        return odeSystem.substitute(parameterValues);
    }

    @Override
    public OdeSystem substitute(ParameterValue... parameterValues) {
        return odeSystem.substitute(parameterValues);
    }

    private static Expression createExpression(ASTNode mathml, Map<String, Variable> variables, Map<String, Parameter> parameters, Map<String, Parameter> localParameters) {
        if (mathml.isReal()) {
            return new Constant((float) mathml.getReal());
        }
        if (mathml.isName()) {
            Variable variable = variables.get(mathml.getName());
            if (variable == null) {
                Parameter parameter = localParameters.get(mathml.getName());
                if (parameter == null) {
                    parameter = parameters.get(mathml.getName());
                    if (parameter == null) {
                        LOGGER.warn("There is no variable or parameter called [" + mathml.getName() + "].");
                    }
                }
                return parameter;
            }
            return variable;
        }
        if (mathml.isOperator() || mathml.isFunction()) {
            if (mathml.getChildCount() == 1) {
                return createExpression(mathml.getChild(0), variables, parameters, localParameters);
            }
            List<Expression> subsList = new ArrayList<>();
            for (int i=0; i<mathml.getChildCount(); i++) {
                Expression exp = createExpression(mathml.getChild(i), variables, parameters, localParameters);
                if (exp != null) {
                    subsList.add(exp);
                }
            }
            Expression[] subs = new Expression[subsList.size()];
            subsList.toArray(subs);
            switch(mathml.getType()) {
                case TIMES:
                    return new Times(subs);
                case DIVIDE:
                    return new Divide(subs);
                case PLUS:
                    return new Plus(subs);
                case MINUS:
                    return new Minus(subs);
                case POWER:
                    return new Power(subs[0], subs[1]);
                case FUNCTION_POWER:
                    return new Power(subs[0], subs[1]);
            }
        }
        throw new IllegalArgumentException("Can't create an expression from [" + mathml.toFormula() + "], its type is [" + mathml.getType() + "].");
    }

    private static OdeSystem setup(Model model) {
        Map<String, Variable> variablesMemory = new HashMap<>();
        Map<String, Parameter> parametersMemory = new HashMap<>();
        List<SubstitutionValue> substitutionValues = new ArrayList<>();
        Map<Variable, List<Expression>> positives = new HashMap<>();
        Map<Variable, List<Expression>> negatives = new HashMap<>();
        List<Variable> variables = new ArrayList<>();
        List<VariableValue> variableValues = new ArrayList<>();
        List<ParameterValue> parameterValues = new ArrayList<>();
        // load variables
        for (Species species : model.getListOfSpecies()) {
            if (species.getId() == null || species.getId().isEmpty()) {
                LOGGER.warn("skipping species with undefined id");
                continue;
            }
            Variable var = new Variable(species.getName() == null || species.getName().isEmpty() ? species.getId() : species.getName(), variables.size());
            variablesMemory.put(species.getId(), var);
            variables.add(var);
            variableValues.add(new VariableValue(var, (float) species.getInitialConcentration()));
        }
        // load paramaters
        for (org.sbml.jsbml.Parameter p : model.getListOfParameters()) {
            if (p.getId() == null || p.getId().isEmpty()) {
                LOGGER.warn("skipping parameter with undefined id");
                continue;
            }
            if (new Double(p.getValue()).equals(Double.NaN)) {
                throw new IllegalStateException("The values of parameter '" + p.getId() + "' is not defined.");
            }
            Parameter param = new Parameter(p.getName() == null || p.getName().isEmpty() ? p.getId() : p.getName(), variables.size() + parametersMemory.size());
            parametersMemory.put(p.getId(), param);
            ParameterValue pv = new ParameterValue(param, (float) p.getValue());
            substitutionValues.add(pv);
            parameterValues.add(pv);
        }
        // load reaction speed
        for (Reaction reaction: model.getListOfReactions()) {
            String reactionName = reaction.getName() == null || reaction.getName().isEmpty() ? reaction.getId() : reaction.getName();
            Map<String, Parameter> localParameters = new HashMap<>();
            for (org.sbml.jsbml.LocalParameter p : reaction.getKineticLaw().getListOfLocalParameters()) {
                if (p.getId() == null || p.getId().isEmpty()) {
                    LOGGER.warn("skipping local parameter with undefined id");
                }
                String paramLocalName = p.getName() == null || p.getName().isEmpty() ? p.getId() : p.getName();
                String paramGloabalName = reactionName + ":" + paramLocalName;
                if (new Double(p.getValue()).equals(Double.NaN)) {
                    throw new IllegalStateException("The value of local paramater '" + paramLocalName + "' in reaction '" + reactionName + "' is not defined.");
                }
                if (parametersMemory.containsKey(paramGloabalName)) {
                    throw new IllegalStateException("Can't load local paramater '" + paramLocalName + "' in reaction '" + reactionName + "', because there is already defined parameter '" + paramGloabalName + "'");
                }
                Parameter parameter = new Parameter(paramGloabalName, variables.size() + parametersMemory.size());
                ParameterValue pv = new ParameterValue(parameter, (float) p.getValue());
                parameterValues.add(pv);
                substitutionValues.add(pv);
                localParameters.put(paramLocalName, parameter);
                parametersMemory.put(paramGloabalName, parameter);

            }
            Expression kineticLaw = createExpression(reaction.getKineticLaw().getMath(), variablesMemory, parametersMemory, localParameters);
            if (kineticLaw == null) {
                throw new IllegalStateException("Can't parse a kinetic law.");
            }
            for (SpeciesReference speciesReference: reaction.getListOfReactants()) {
                if (speciesReference.getSpecies() == null || speciesReference.getSpecies().isEmpty()) {
                    LOGGER.warn("skipping species reference with undefined species");
                    continue;
                }
                Variable variable = variablesMemory.get(speciesReference.getSpecies());
                if (!negatives.containsKey(variable)) {
                    negatives.put(variable, new ArrayList<Expression>());
                }
                negatives.get(variable).add(kineticLaw);
            }
            for (SpeciesReference speciesReference: reaction.getListOfProducts()) {
                if (speciesReference.getSpecies() == null || speciesReference.getSpecies().isEmpty()) {
                    LOGGER.warn("skipping species reference with undefined species");
                    continue;
                }
                Variable variable = variablesMemory.get(speciesReference.getSpecies());
                if (!positives.containsKey(variable)) {
                    positives.put(variable, new ArrayList<Expression>());
                }
                positives.get(variable).add(kineticLaw);
            }
            if (reaction.isReversible()) {
                LOGGER.warn("The reversible reactions are not supported.");
            }
        }
        List<OdeSystemVariable> result = new ArrayList<>();
        // construct right sides
        for (Variable variable: variables) {
            List<Expression> pos = positives.get(variable);
            List<Expression> neg = negatives.get(variable);
            Expression rs = null;
            if (pos != null) {
                for (Expression e: pos) {
                    if (rs == null) {
                        rs = e;
                    } else {
                        rs = new Plus(rs, e);
                    }
                }
            }
            if (neg != null) {
                for (Expression e: neg) {
                    if (rs == null) {
                        rs = new Negation(e);
                    } else {
                        rs = new Minus(rs, e);
                    }
                }
            }
            if (rs == null) {
                rs = new Constant(0);
            }
            result.add(new OdeSystemVariable(variable, rs.substitute(substitutionValues)));
        }
        return new SimpleOdeSystem(result, variableValues, parameterValues);
    }

}