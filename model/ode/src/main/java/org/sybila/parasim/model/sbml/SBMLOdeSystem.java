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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sbml.jsbml.ASTNode;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
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
import org.sybila.parasim.model.math.Plus;
import org.sybila.parasim.model.math.Power;
import org.sybila.parasim.model.math.Times;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.math.VariableValue;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.ode.OdeSystemVariable;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class SBMLOdeSystem implements OdeSystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(SBMLOdeSystem.class);
    private final Model model;
    private List<OdeSystemVariable> odeSystemVariables = new ArrayList<>();

    public SBMLOdeSystem(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("The parameter [model] is null.");
        }
        this.model = model;
        setup();
    }

    @Override
    public int dimension() {
        return odeSystemVariables.size();
    }

    @Override
    public OdeSystemVariable getVariable(int dimension) {
        return odeSystemVariables.get(dimension);
    }

    @Override
    public Iterator<OdeSystemVariable> iterator() {
        return odeSystemVariables.iterator();
    }

    private Expression createExpression(ASTNode mathml, Map<String, Variable> variables) {
        if (mathml.isReal()) {
            return new Constant((float) mathml.getReal());
        }
        if (mathml.isName()) {
            Variable variable = variables.get(mathml.getName());
            if (variable == null) {
                throw new IllegalArgumentException("There is no variable called [" + mathml.getName() + "].");
            }
            return variable;
        }
        if (mathml.isOperator() || mathml.isFunction()) {
            if (mathml.getChildCount() == 1) {
                return createExpression(mathml.getChild(0), variables);
            }
            Expression[] subs = new Expression[mathml.getChildCount()];
            for (int i=0; i<subs.length; i++) {
                subs[i] = createExpression(mathml.getChild(i), variables);
            }
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

    private void setup() {
        Map<String, Variable> variablesMemory = new HashMap<>();
        List<VariableValue> variableValuesMemory = new ArrayList<>();
        Map<Variable, List<Expression>> positives = new HashMap<>();
        Map<Variable, List<Expression>> negatives = new HashMap<>();
        List<Variable> variables = new ArrayList<>();
        Set<String> paramNames = new HashSet<>();
        // load variables
        for (Species species : model.getListOfSpecies()) {
            Variable var = new Variable(species.getId(), variables.size());
            variablesMemory.put(species.getId(), var);
            variables.add(var);
        }
        // load paramaters
        for (Parameter p : model.getListOfParameters()) {
            Variable var = new Variable(p.getId(), variables.size());
            variablesMemory.put(p.getId(), var);
            variables.add(var);
            variableValuesMemory.add(new VariableValue(var, (float) p.getValue()));
            paramNames.add(p.getId());
        }
        // load reaction speed
        for (Reaction reaction: model.getListOfReactions()) {
            Expression kineticLaw = createExpression(reaction.getKineticLaw().getMath(), variablesMemory);
            for (SpeciesReference speciesReference: reaction.getListOfReactants()) {
                Variable variable = variablesMemory.get(speciesReference.getSpecies());
                if (!negatives.containsKey(variable)) {
                    negatives.put(variable, new ArrayList<Expression>());
                }
                negatives.get(variable).add(kineticLaw);
            }
            for (SpeciesReference speciesReference: reaction.getListOfProducts()) {
                Variable variable = variablesMemory.get(speciesReference.getSpecies());
                if (!positives.containsKey(variable)) {
                    positives.put(variable, new ArrayList<Expression>());
                }
                positives.get(variable).add(kineticLaw);
            }
            if (reaction.isReversible()) {
                LOGGER.warn("The reversible reactions are not fully supported.");
            }
        }
        // construct right sides
        for (Variable variable: variables) {
            if (paramNames.contains(variable.getName())) {
                continue;
            }
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
            odeSystemVariables.add(new OdeSystemVariable(variable, rs.substitute(variableValuesMemory)));
        }
    }

}