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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.sbml.jsbml.AssignmentRule;
import org.sbml.jsbml.Compartment;
import org.sbml.jsbml.FunctionDefinition;
import org.sbml.jsbml.InitialAssignment;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.RateRule;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Rule;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Function;
import org.sybila.parasim.model.math.Minus;
import org.sybila.parasim.model.math.Negation;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.Plus;
import org.sybila.parasim.model.math.SubstitutionValue;
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

    // TODO refactor
    private static OdeSystem setup(Model model) {
        Map<String, Variable> variablesMemory = new HashMap<>();
        Map<String, Parameter> parametersMemory = new HashMap<>();
        Map<String, Expression> globals = new HashMap<>();
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
            Variable var = new Variable(species.getId(), variables.size());
            variablesMemory.put(species.getId(), var);
            variables.add(var);
            if (!new Double(species.getInitialConcentration()).equals(Double.NaN)) {
                variableValues.add(new VariableValue(var, (float) species.getInitialConcentration()));
            } else if (!new Double(species.getInitialAmount()).equals(Double.NaN)) {
                variableValues.add(new VariableValue(var, (float) species.getInitialAmount()));
            } else {
                throw new IllegalStateException("The species [" + species.getId() + "] has no defined initial concentration or amount.");
            }
            globals.put(species.getId(), var);
        }
        // load paramaters
        for (org.sbml.jsbml.Parameter p : model.getListOfParameters()) {
            if (p.getId() == null || p.getId().isEmpty()) {
                LOGGER.warn("skipping parameter with undefined id");
                continue;
            }
            if (new Double(p.getValue()).equals(Double.NaN)) {
                LOGGER.warn("The value of parameter '" + p.getId() + "' is not defined, so skipping it.");
                continue;
            }
            Parameter param = new Parameter(p.getId(), variables.size() + parametersMemory.size());
            parametersMemory.put(p.getId(), param);
            ParameterValue pv = new ParameterValue(param, (float) p.getValue());
            substitutionValues.add(pv);
            parameterValues.add(pv);
            globals.put(p.getId(), param);

        }
        // load compartments
        for (Compartment compartment: model.getListOfCompartments()) {
            if (compartment.getId() == null || compartment.getId().isEmpty()) {
                LOGGER.warn("skipping compartment with undefined id");
                continue;
            }
            if (new Double(compartment.getValue()).equals(Double.NaN)) {
                LOGGER.warn("skipping compartment ["+compartment.getId()+"] with undefined value");
            }
            globals.put(compartment.getId(), new Constant((float) compartment.getValue()));
        }
        // load functions
        for (FunctionDefinition function: model.getListOfFunctionDefinitions()) {
            if (function.getId() == null || function.getId().isEmpty()) {
                LOGGER.warn("skipping function with undefined id");
                continue;
            }
            Map<String, Expression> locals = new HashMap<>();
            List<Parameter> arguments = new ArrayList<>();
            for (int i=0; i<function.getNumArguments(); i++) {
                String localName = function.getArgument(i).getName();
                String globalName = function.getId() + ":" + localName;
                Parameter argument = new Parameter(globalName, variables.size() + parametersMemory.size());
                locals.put(localName, argument);
                arguments.add(argument);
            }
            globals.put(function.getId(), new Function(function.getId(), new SBMLMathFactory().createExpression(function.getBody(), globals, locals), arguments));
        }
        // assignments
        for (InitialAssignment assignment: model.getListOfInitialAssignments()) {
            String name = assignment.getVariable();
            Expression expression = new SBMLMathFactory().createExpression(assignment.getMath(), globals, Collections.EMPTY_MAP);
            globals.put(name, expression);
        }
        // rules
        for (Rule rule: model.getListOfRules()) {
            if (!(rule instanceof AssignmentRule)) {
                LOGGER.warn("only assignment rules are supported, skipping rule with metaid [" + rule.getMetaId() + "]");
                continue;
            }
            AssignmentRule assignmentRule = (AssignmentRule) rule;
            String name = assignmentRule.getVariable();
            Expression expression = new SBMLMathFactory().createExpression(assignmentRule.getMath(), globals, Collections.EMPTY_MAP);
            globals.put(name, expression);
        }
        // load reaction speed
        for (Reaction reaction: model.getListOfReactions()) {
            String reactionName = reaction.getId();
            Map<String, Expression> locals = new HashMap<>();
            LOGGER.debug("parsing reaction with id = " + reaction.getId());
            if (reaction.getKineticLaw() == null)  {
                LOGGER.warn("skipping reaction (id = '" + reaction.getId() + "') without any kinetic law");
                continue;
            }
            for (org.sbml.jsbml.LocalParameter p : reaction.getKineticLaw().getListOfLocalParameters()) {
                if (p.getId() == null || p.getId().isEmpty()) {
                    LOGGER.warn("skipping local parameter with undefined id in reaction with id = " + reaction.getId() + ".");
                }
                String paramLocalName = p.getId();
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
                locals.put(paramLocalName, parameter);
                parametersMemory.put(paramGloabalName, parameter);

            }
            Expression kineticLaw = new SBMLMathFactory().createExpression(reaction.getKineticLaw().getMath(), globals, locals);
            if (kineticLaw == null) {
                throw new IllegalStateException("Can't parse a kinetic law in reaction with id = " + reaction.getId() + ".");
            }
            for (SpeciesReference speciesReference: reaction.getListOfReactants()) {
                if (speciesReference.getSpecies() == null || speciesReference.getSpecies().isEmpty()) {
                    LOGGER.warn("skipping species reference with undefined species in reaction with id = " + reaction.getId() + ".");
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