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
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration precision) {
        Model model = odeSystem.getOriginalModel();

        System.out.println("PARAMETER VALUES: " + odeSystem.getAvailableParameters() +" VARIABLES: " + odeSystem.getVariables());
        // DONE Vojta - how to recognize if the index is same as in my Model (corresponding parameters and variables)

        //SETTING VARIABLES
        for(Variable variable : odeSystem.getVariables().values()){
            System.out.println(variable.getName() + " - VALUE: " + variable.evaluate(point));
            System.out.println("INITIAL: " + point.getValue(odeSystem.getInitialVariableValue(variable).getExpression().getIndex()));
            if (!variable.isSubstituted()) {
                //set species (variables) values in model
                model.getSpecies(variable.getName()).setValue(point.getValue(variable.getIndex()));
                //TODO throw exceptions if no variables with this name are found?
            }
        }
        //odeSystem.getAvailableParameters() returns only parameters, use odeSystem.getVariables() to get variables
        //DONE findOut if parameters are also variables (if it is synonym)
        ////TODO Vojta - how does initial conditions setting work (or perturbating over variables not parameters) ->need to debug on linux?

        //SETTING PARAMETERS
        for(Parameter parameter : odeSystem.getAvailableParameters().values()){
            if (!parameter.isSubstituted()) {
                System.out.println(parameter.getName() + " - VALUE: " + point.getValue(parameter.getIndex()));
                //set parameters values in model
                model.getParameter(parameter.getName()).setValue(point.getValue(parameter.getIndex()));//what is the difference?? "parameter.evaluate(point)" - doesnt work if parameter is not substituted vs "point.getValue(parameter.getIndex())" - works
            }
        }
        //DONE Vojta - set parameter value in model according to ode system parameter value
        //DONE Vojta - create substituted odeSystem (model)

        //SIMULATION
        SBMLinterpreter interpreter = null;
        try {
            interpreter = new SBMLinterpreter(model);
        } catch (ModelOverdeterminedException e) {
            e.printStackTrace();
        }

        //TODO find out what is 'size' parameter
        AbstractDESSolver solver = new RosenbrockSolver(0, precision.getTimeStep()); //TODO Vojta - set timestep
        //DONE Vojta - where to get time array or start time and end time
        MultiTable solution = null;
        try {
            solution = solver.solve(interpreter, interpreter.getInitialValues(), point.getTime(), timeLimit);
            //TODO how to set start and end time correctly
            //start: point.getTime() ?
            //end: timeLimit?
        } catch (DerivativeException e) {
            e.printStackTrace();
        }


        //PARSING DATA TO TRAJECTORY
        //TODO Vojta - how to create new trajectory from multitable
        if (solution == null) {
            //throw exception
        }
        System.out.println("blocks: " + solution.getBlockCount() + " columns: " + solution.getColumnCount());
        //doubles to float
//        float[] data = new float[];
//        for (int dim = 0; dim < octaveOdeSystem.dimension(); dim++) {
//            for (int i = 0; i < loadedData.length / octaveOdeSystem.dimension(); i++) {
//                data[dim + i * solution.getBlockCount()] = (float) solution[dim * (loadedData.length / octaveOdeSystem.dimension()) + i];
//            }
//        }
//
//        float[] times = new float[solution.getTimePoints().length];
//        float time = point.getTime();
//        for (int i = 0; i < times.length; i++) {
//            time += precision.getTimeStep();
//            times[i] = time;
//        }
//        if (odeSystem.getAvailableParameters().isEmpty()) {
//            return new ArrayTrajectory(data, times, point.getDimension());
//        } else {
//            return new ArrayTrajectory(point, data, times, octaveOdeSystem.dimension());
//        }
        return null;
    }
}
