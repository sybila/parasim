package org.sybila.parasim.computation.simulation.simulationcore;

import org.apache.commons.math.ode.DerivativeException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.validator.ModelOverdeterminedException;
import org.simulator.math.odes.AbstractDESSolver;
import org.simulator.math.odes.AdaptiveStepsizeIntegrator;
import org.simulator.math.odes.MultiTable;
import org.simulator.math.odes.RosenbrockSolver;
import org.simulator.sbml.SBMLinterpreter;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.SimulationException;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.model.math.Parameter;
import org.sybila.parasim.model.math.Variable;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.*;


/**
 * @author Vojtech Bruza
 */
public class SimCoreSimulationEngine implements SimulationEngine {


    //TODO Vojta - add getStepLimit()

    @Override
    public void close() {
        //TODO dont have to close this one - remove from SimulationEngine interface
    }

    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration precision) {
        //start time: point.getTime() ?
        //end time: timeLimit?

        Model model = odeSystem.getOriginalModel();

//        System.out.println("PARAMETER VALUES: " + odeSystem.getAvailableParameters().keySet() +" VARIABLES: " + odeSystem.getVariables().keySet());
//        // DONE Vojta - how to recognize if the index is same as in my Model (corresponding parameters and variables)
//
//        //SETTING VARIABLES
//        for(Variable variable : odeSystem.getVariables().values()){
//            System.out.println(variable.getName() + " - VALUE: " + variable.evaluate(point));
//            System.out.println("INITIAL: " + point.getValue(odeSystem.getInitialVariableValue(variable).getExpression().getIndex()));
//            if (!variable.isSubstituted()) {
//                //set species (variables) values in model
//                model.getSpecies(variable.getName()).setValue(point.getValue(variable.getIndex()));
//            }
//        }
        //DONE!! SET VARIABLES
        for(Variable variable : odeSystem.getVariables().values()){
            if (!variable.isSubstituted()) {
                //set species (variables) values in model
                model.getSpecies(variable.getName()).setValue(point.getValue(variable.getIndex()));
                //TODO throw exceptions if no variables with this name are found?
                //TODO Vojta - how does initial conditions setting work (or perturbating over variables not parameters) ->need to debug on linux? //I think it works like expected (get set values from the current point)
            }
        }

//        //odeSystem.getAvailableParameters() returns only parameters, use odeSystem.getVariables() to get variables
//        //DONE findOut if parameters are also variables (if it is synonym)
//
//        //SETTING PARAMETERS
//        for(Parameter parameter : odeSystem.getAvailableParameters().values()){
//            if (!parameter.isSubstituted()) {
//                System.out.println(parameter.getName() + " - VALUE: " + point.getValue(parameter.getIndex()));
//                //set parameters values in model
//                model.getParameter(parameter.getName()).setValue(point.getValue(parameter.getIndex()));//what is the difference?? "parameter.evaluate(point)" - doesnt work if parameter is not substituted vs "point.getValue(parameter.getIndex())" - works
//            }
//        }
//        //DONE Vojta - set parameter value in model according to ode system parameter value
//        //DONE Vojta - create substituted odeSystem (model)
//

        //DONE!! SET PARAMETERS
        for(Parameter parameter : odeSystem.getAvailableParameters().values()){
            if (!parameter.isSubstituted()) { //substituted are those that are set at the beginning (not perturbating over them)
                //set parameters values in model
                model.getParameter(parameter.getName()).setValue(point.getValue(parameter.getIndex()));//what is the difference?? "parameter.evaluate(point)" - doesnt work if parameter is not substituted vs "point.getValue(parameter.getIndex())" - works
            }
        }


        //TODO!! SET SIMULATION TIME (NUM OF ITERATIONS) - START, END, NUM OF ITERATION, TIME STEP
        long numOfIterations = Math.round(Math.ceil((1.05 * timeLimit - point.getTime()) / precision.getTimeStep())); //magical constant 1.05 taked from Jan Papousek's implementation => guess it is for rendering reasons (to simulate a bit more data than a user excpects)
//        if (numOfIterations > getStepLimit()) { //TODO max num iterations limit is Integer.MAX_VALUE because I have to change the type to integer
//            throw new IllegalStateException("Can't simulate the trajectory because the number of iterations <" + numOfIterations + "> is higher than the given limit <" + getStepLimit() + ">.");
//        }
        //TIME - need to be float //TODO use only double? ask Safranek
        float[] timesFloat = new float[(int) numOfIterations];
        float timeFloat = point.getTime();
        for (int j = 0; j < timesFloat.length; j++) {
            timeFloat += precision.getTimeStep();
            timesFloat[j] = timeFloat;
        } //TODO remove redundant code (twice creating time array)
        double[] times = new double[(int) numOfIterations];
        double time = point.getTime();
        for (int j = 0; j < times.length; j++) {
            time += precision.getTimeStep();
            times[j] = time;
        }

        //DONE!! SIMULATION
        //SIMULATION
        SBMLinterpreter interpreter = null;
        try {
            interpreter = new SBMLinterpreter(model);
        } catch (ModelOverdeterminedException e) {
            e.printStackTrace();
        }
        if(interpreter == null){
            //TODO throw exception
        }

//        //TODO find out what is 'size' parameter -> use this constructor
//        AbstractDESSolver solver = new RosenbrockSolver(0, precision.getTimeStep());
//        in case of useage of the second constructor: solution = solver.solve(interpreter, interpreter.getInitialValues(), point.getTime(), timeLimit);

//        //DONE Vojta - where to get time array or start time and end time

        AdaptiveStepsizeIntegrator solver = new RosenbrockSolver();
        MultiTable solution = null;

        //DONE!! SET MAX REALTIVE
        solver.setRelTol(precision.getMaxRelativeError());
        //TODO SET MAX ABSOLUTE ERROR
//        float maxAbsoluteError = precision.getMaxAbsoluteError(0);
//        for (int i = 0; i < precision.getDimension(); i++){
//            if(precision.getMaxAbsoluteError(i) < maxAbsoluteError){
//                maxAbsoluteError = precision.getMaxAbsoluteError(i); //taking the smallest error
//            }
//        }
//        solver.setAbsTol(maxAbsoluteError); //TODO correct dimension setting???
        try {
            solution = solver.solve(interpreter, interpreter.getInitialValues(), times);
        } catch (DerivativeException e) {
            e.printStackTrace();
        }
//        if (solution == null) {
////            //TODO throw exception?
//        }

        //DONE!! DATA FROM MULTITABLE TO FLOAT ARRAY
//        //PARSING DATA TO TRAJECTORY
        //help printing
//        for (int i = 0; i < solution.getBlockCount(); i++) {
//            System.out.println("block" + i + " name: " + solution.getBlock(i).getName());
//            for (int c = 1; c < solution.getBlock(i).getColumnCount(); c++) {
//                System.out.println("\tcolumn" + c + " name: " + solution.getBlock(i).getColumn(c).getColumnName());
//            }
//        }
//        System.out.println("COLUMNS:");
//        for (Parameter p : odeSystem.getAvailableParameters().values()){
//            System.out.println("parameter: " + p.getName() + " column: " + solution.getColumn(p.getName()).getColumnName());
//        }
//        for (Variable v : odeSystem.getVariables().values()){
//            System.out.println("variable: " + v.getName() + " column: " + solution.getColumn(v.getName()).getColumnName());
//        }
//        System.out.println("Start time: " + point.getTime() + " End Time: " + timeLimit);
//        System.out.println("Number of iterations: " + numOfIterations);

        int numberOfSteps = 0;
        try{
            numberOfSteps = solution.getRowCount();
        } catch (NullPointerException e) {
            e.printStackTrace(); //if solver failed and solution is null
        }
        float[] simulatedData = new float[numberOfSteps*odeSystem.getVariables().size()];
        for (int currentVariable = 0; currentVariable < odeSystem.getVariables().size(); currentVariable++) { //simulating only variables, not parameters
//            System.out.println(odeSystem.getVariable(currentVariable).getName());
            for (int i = 0; i < solution.getRowCount(); i++) {
//                System.out.println("row: " + i + " time: " + times[i] + " value: " + solution.getColumn(odeSystem.getVariable(currentVariable).getName()).getValue(i));
                simulatedData[currentVariable + i * odeSystem.getVariables().size()] = (float) solution.getColumn(odeSystem.getVariable(currentVariable).getName()).getValue(i);
            }
        }


        //TODO - top priority - verification of sizes of arrays, dimensions and so
        //DONE!! OUTPUT TRAJECTORY
        //DONE Vojta - how to create new trajectory from multitable - correct data parsing
        if (odeSystem.getAvailableParameters().isEmpty()) {
            return new ArrayTrajectory(simulatedData, timesFloat, point.getDimension());
        } else {
            return new ArrayTrajectory(point, simulatedData, timesFloat, odeSystem.getVariables().size());
        }
    }
}
