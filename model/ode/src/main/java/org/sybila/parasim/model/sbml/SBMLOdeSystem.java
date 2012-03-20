package org.sybila.parasim.model.sbml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sbml.jsbml.ASTNode.Type;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Parameter;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import org.sybila.parasim.model.ode.OdeSystemEncoding;
import org.sybila.parasim.model.ode.AbstractOdeSystem;
import org.sybila.parasim.model.ode.ArrayOdeSystemEncoding;
import org.sybila.parasim.model.ode.Variable;
import org.sybila.parasim.model.trajectory.Point;

/**
 * @author <a href="mailto:jpapouse@fi.muni.cz">Jan Papousek</a>
 */
public class SBMLOdeSystem extends AbstractOdeSystem {

    private OdeSystemEncoding encoding;
    private Model model;
    private List<Variable> variables;
    
    public SBMLOdeSystem(Model model) {
        if (model == null) {
            throw new IllegalArgumentException("The parameter [model] is null.");
        }
        this.model = model;
        setup();
    }

    public int dimension() {
        return variables.size();
    }

    public Variable getVariable(int dimension) {
        return variables.get(dimension);
    }

    public OdeSystemEncoding encoding() {
        return encoding;
    }
    
    private void setup() {
        // map containing all variables: name -> variable instance
        Map<String, Variable> variablesMemory = new HashMap<String, Variable>(model.getListOfSpecies().size());
        // map containing all parameter: name -> parameter instance
        Map<String, Parameter> paramsMemory = new HashMap<String, Parameter>();
        // memorize products for each reaction
        Map<Variable, List<List<Variable>>> factors = new HashMap<Variable, List<List<Variable>>>();
        // memorize constant coefficients for each reaction
        Map<Variable, List<Double>> coefficients = new HashMap<Variable, List<Double>>();
        // memorize number of coefficients at all
        int numberOfCoefficients = 0;
        // species ~ variables
        variables = new ArrayList<Variable>(model.getListOfSpecies().size());
        // load variables
        for (Species species : model.getListOfSpecies()) {
            Variable var = new Variable(species.getId(), variables.size());
            variablesMemory.put(species.getId(), var);
            variables.add(var);
            factors.put(var, new ArrayList<List<Variable>>());
            coefficients.put(var, new ArrayList<Double>());
        }
        // load paramaters
        for (Parameter p : model.getListOfParameters()) {
            paramsMemory.put(p.getId(), p);
        }
        // load reaction speed
        for (Reaction reaction : model.getListOfReactions()) {
            double coefficient = 1;
            List<Variable> currentProducts = new ArrayList<Variable>();
            // if the kinetic law is real number, just take it
            if (reaction.getKineticLaw().getMath().isReal()) {
                coefficient = reaction.getKineticLaw().getMath().getReal();
            // if the kinetic law is a function (only TIMES is supported), take list of products and compute coefficient
            } else if (reaction.getKineticLaw().getMath().getType().equals(Type.TIMES)) {
                String[] varNames = reaction.getKineticLaw().getMath().toFormula().split("\\*");
                for (int i = 0; i < varNames.length; i++) {
                    String varName = varNames[i].trim();
                    if (paramsMemory.containsKey(varName)) {
                        coefficient *= paramsMemory.get(varName).getValue();
                    } else if (variablesMemory.containsKey(varName)) {
                        currentProducts.add(variablesMemory.get(varName));
                    } else {
                        throw new IllegalArgumentException("The kinetic law can't be processed, because the uknown variable [" + varName + "].");
                    }
                }
            // Not supported
            } else {
                throw new IllegalStateException("The kinetic law has to be a number or TIMES type.");
            }
            // reversible reaction
            List<Variable> currentProductsReverse = new ArrayList<Variable>();
            if (reaction.isReversible()) {
                currentProductsReverse.addAll(currentProducts);
                for (int i = 0; i < reaction.getListOfProducts().size(); i++) {
                    currentProductsReverse.add(variablesMemory.get(reaction.getListOfProducts().get(i).getSpecies()));
                }
            }
            for (int i = 0; i < reaction.getListOfReactants().size(); i++) {
                currentProducts.add(variablesMemory.get(reaction.getListOfReactants().get(i).getSpecies()));
            }
            // memorize reaction as ODE
            for (SpeciesReference p : reaction.getListOfProducts()) {
                Variable var = variablesMemory.get(p.getSpecies());
                factors.get(var).add(currentProducts);
                coefficients.get(var).add(coefficient); 
                numberOfCoefficients++;
                if (reaction.isReversible()) {
                    factors.get(var).add(currentProductsReverse);
                    coefficients.get(var).add(-coefficient);
                    numberOfCoefficients++;
                }
            }
            for (SpeciesReference p : reaction.getListOfReactants()) {
                Variable var = variablesMemory.get(p.getSpecies());
                factors.get(var).add(currentProducts);
                coefficients.get(var).add(-coefficient); 
                numberOfCoefficients++;
                if (reaction.isReversible()) {
                    factors.get(var).add(currentProductsReverse);
                    coefficients.get(var).add(coefficient);
                    numberOfCoefficients++;
                }
            }
        }
        // build coefficients
        float[] coefficientsInEncoding = new float[numberOfCoefficients];
        int[] factorIndexesInEncoding = new int[numberOfCoefficients+1];
        int[] coefficientIndexesInEncoding = new int[variables.size()+1];
        factorIndexesInEncoding[0] = 0;
        coefficientIndexesInEncoding[0] = 0;
        int currentCoef = 0;
        int numberOfFactors = 0;
        for (Variable var : variables) {
            coefficientIndexesInEncoding[var.getIndex()+1] += coefficientIndexesInEncoding[var.getIndex()] + coefficients.get(var).size();
            for (int i=0; i<coefficients.get(var).size(); i++) {
                coefficientsInEncoding[currentCoef] = (float) (double) coefficients.get(var).get(i);
                numberOfFactors += factors.get(var).get(i).size();
                factorIndexesInEncoding[currentCoef+1] = numberOfFactors;
                currentCoef++;
            }
        }
        // build factors
        int[] factorsInEncoding = new int[numberOfFactors];
        int currentFactor = 0;
        for (Variable var : variables) {
            for (List<Variable> vf : factors.get(var)) {
                for (Variable factor : vf) {
                    factorsInEncoding[currentFactor] = factor.getIndex();
                    currentFactor++;
                }
            }
        }
        this.encoding = new ArrayOdeSystemEncoding(coefficientIndexesInEncoding, coefficientsInEncoding, factorIndexesInEncoding, factorsInEncoding);
    }
}