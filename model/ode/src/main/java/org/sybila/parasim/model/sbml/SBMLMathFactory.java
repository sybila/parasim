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
import java.util.List;
import java.util.Map;
import org.sbml.jsbml.ASTNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sybila.parasim.model.math.Constant;
import org.sybila.parasim.model.math.Divide;
import org.sybila.parasim.model.math.Expression;
import org.sybila.parasim.model.math.Function;
import org.sybila.parasim.model.math.Minus;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.Plus;
import org.sybila.parasim.model.math.Power;
import org.sybila.parasim.model.math.SubstitutionValue;
import org.sybila.parasim.model.math.Times;

public class SBMLMathFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SBMLMathFactory.class);

    public Expression createExpression(ASTNode mathml, Map<String, Expression> globals, Map<String, Expression> locals) {
        // constant
        if (mathml.isReal() || mathml.isInteger()) {
            return new Constant((float) mathml.getReal());
        }
        // named expression
        if (mathml.isName()) {
            Expression expression = locals.get(mathml.getName());
            if (expression == null) {
                expression = globals.get(mathml.getName());
                if (expression == null) {
                    LOGGER.warn("There is no expression called [" + mathml.getName() + "]. Available gloabal expressions are " + globals.keySet() + ", local expressions are " + locals.keySet() + ".");
                }
            }
            return expression;
        }
        // function / operator
        if (mathml.isOperator() || mathml.isFunction()) {
            if (mathml.getChildCount() == 1) {
                return createExpression(mathml.getChild(0), globals, locals);
            }
            List<Expression> subsList = new ArrayList<>();
            for (int i=0; i<mathml.getChildCount(); i++) {
                Expression exp = createExpression(mathml.getChild(i), globals, locals);
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
                case FUNCTION_ROOT:
                    return new Power(subs[1], new Divide(new Constant(1), subs[0]));
                case FUNCTION:
                    Expression expression = globals.get(mathml.getName());
                    if (expression == null) {
                        throw new IllegalStateException("There is no expression called " + mathml.getName() + ". Available global expressions are " + globals.keySet());
                    }
                    if (!(expression instanceof Function)) {
                        throw new IllegalStateException("The xpression called " + mathml.getName() + " is not a function.");
                    }
                    Function function = (Function) expression;
                    if (function.getArguments().size() != subs.length) {
                        throw new IllegalStateException("The number of arguments [" + subs.length + "] doesn't match to the arity [" + function.getArguments().size() + "] of function " + function.getName());
                    }
                    List<SubstitutionValue<Parameter>> toSubstitute = new ArrayList<>();
                    for (int i=0; i<subs.length; i++) {
                        toSubstitute.add(new ParameterValue(function.getArguments().get(i), subs[i]));
                    }
                    return function.getBody().substitute(toSubstitute);
            }
        }
        throw new IllegalArgumentException("Can't create an expression from [" + mathml.toFormula() + "], its type is [" + mathml.getType() + "].");
    }

}
