package org.sybila.parasim.computation.simulation.simulationcore;

import org.apache.commons.math.ode.DerivativeException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.validator.ModelOverdeterminedException;
import org.simulator.math.odes.AbstractDESSolver;
import org.simulator.math.odes.MultiTable;
import org.simulator.math.odes.RosenbrockSolver;
import org.simulator.sbml.SBMLinterpreter;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.ParameterValue;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vojtech Bruza
 */
public class SimCoreSimulationEngine implements SimulationEngine {


    @Override
    public void close() {
        //TODO
    }


    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration configuration) {
        Model model = odeSystem.getOriginalModel();

        System.out.println("PARAMETER VALUES: " + odeSystem.getAvailableParameters() +" VARIABLES: " + odeSystem.getVariables());

        //SETTING VARIABLES
        for(Variable variable : odeSystem.getVariables().values()){
            System.out.println(variable.getName() + " - VALUE: " + variable.evaluate(point));
            System.out.println("INITIAL: " + odeSystem.getInitialVariableValue(variable).getExpression().evaluate(point));
            if (!variable.isSubstituted()) {
                //set species (variables) values in model
                model.getParameter(variable.getName()).setValue(variable.evaluate(point));
            }
        }
        //SETTING PARAMETERS
        for(Parameter parameter : odeSystem.getAvailableParameters().values()){
            if (!parameter.isSubstituted()) {
                System.out.println(parameter.getName() + " - VALUE: " + parameter.evaluate(point));
                //set parameters values in model
                model.getParameter(parameter.getName()).setValue(parameter.evaluate(point));//what is the difference?? "parameter.evaluate(point)" vs "point.getValue(parameter.getIndex())"
            }
        }
        //DONE Vojta - set parameter value in model according to ode system parameter value

        //odeSystem.getAvailableParameters() returns only parameters, use odeSystem.getVariables() to get variables
        ////TODO Vojta - how does initial conditions setting work (or perturbating over variables not parameters)

        // TODO Vojta - how to recognize if the index is same as in my Model (corresponding parameters and variables)
        //TODO findOut if parameters are also variables (if it is synonym)
        //TODO create substituted odeSystem (model)
        // create substituted ode system


        SBMLinterpreter interpreter = null;
        try {
            interpreter = new SBMLinterpreter(model);
        } catch (ModelOverdeterminedException e) {
            e.printStackTrace();
        }

        AbstractDESSolver solver = new RosenbrockSolver();
        double[] timePoints = {0,1,2,3,4,5,6,7,8,9,10}; //TODO Vojta - where to get time array
        MultiTable solution = null;
        try {
            solution = solver.solve(interpreter, interpreter.getInitialValues(), timePoints);
        } catch (DerivativeException e) {
            e.printStackTrace();
        }

        //TODO Vojta - how to create new trajectory
        return null;
    }
}
