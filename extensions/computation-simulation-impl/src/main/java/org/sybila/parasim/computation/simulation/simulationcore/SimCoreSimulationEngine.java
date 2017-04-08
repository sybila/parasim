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


    //TODO Vojta - add getStepLimit()

    @Override
    public void close() {
        //TODO ?
    }

    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration precision) {
        Model model = odeSystem.getOriginalModel();

        System.out.println("PARAMETER VALUES: " + odeSystem.getAvailableParameters().keySet() +" VARIABLES: " + odeSystem.getVariables().keySet());
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

//        long numOfIterations = Math.round(Math.ceil((1.05 * timeLimit - point.getTime()) / precision.getTimeStep()));
//        if (numOfIterations > getStepLimit()) {
//            throw new IllegalStateException("Can't simulate the trajectory because the number of iterations <" + numOfIterations + "> is higher than the given limit <" + getStepLimit() + ">.");
//        }

        //TIME - need to be float
        double[] times = new double[(int)Math.round(Math.ceil((1.05 * timeLimit - point.getTime()) / precision.getTimeStep()))]; //TODO rewrite this - compute correct time length of simulation
        double time = point.getTime();
        for (int j = 0; j < times.length; j++) {
            time += precision.getTimeStep();
            times[j] = time;
        }
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
            solution = solver.solve(interpreter, interpreter.getInitialValues(), times);
            //TODO!!!! how to set start and end time correctly
            //start: point.getTime() ?
            //end: timeLimit?
        } catch (DerivativeException e) {
            e.printStackTrace();
        }


        //PARSING DATA TO TRAJECTORY
        //TODO Vojta - how to create new trajectory from multitable - correct data parsing
        if (solution == null) {
            //throw exception
        }

        //HELP PRINTING
        for (int i = 0; i < solution.getBlockCount(); i++) {
            System.out.println("block" + i + " name: " + solution.getBlock(i).getName());
            for (int c = 1; c < solution.getBlock(i).getColumnCount(); c++) {
                System.out.println("\tcolumn" + c + " name: " + solution.getBlock(i).getColumn(c).getColumnName());
            }
        }
        System.out.println("COLUMNS:");
        for (Parameter p : odeSystem.getAvailableParameters().values()){
            System.out.println("parameter: " + p.getName() + " column: " + solution.getColumn(p.getName()).getColumnName());
        }
        for (Variable v : odeSystem.getVariables().values()){
            System.out.println("variable: " + v.getName() + " column: " + solution.getColumn(v.getName()).getColumnName());
        }

        //DOUBLES TO FLOATS //TODO correct and optimise loading data from multitable
        float[] data = new float[odeSystem.dimension() * solution.getTimePoints().length];
        //DONE figure out the correct format of data
        //timePoints array is to big? larger than simulation

        int dim = 0;
        for (Variable v : odeSystem.getVariables().values()){
            for (int i = 0; i < solution.getColumn(v.getName()).getRowCount(); i++){
////////////////TODO correct data retrival TODOTODOTODOTODOTODOTODOTODO!!!!!!!!!!! maybe works but needs to check
                double d = solution.getColumn(v.getName()).getValue(i);
                data[i * odeSystem.getVariables().size() + dim] = (float) d;
            }
            dim++;
        }

//        solution.getTimePoints() == solution.getColumn(0)

        //TIME - TODO fix duplicate code of time array
        float[] timesFloat = new float[(int)Math.round(Math.ceil((1.05 * timeLimit - point.getTime()) / precision.getTimeStep()))]; //TODO rewrite this - compute correct time length of simulation
        float timefloat = point.getTime();
        for (int j = 0; j < timesFloat.length; j++) {
            timefloat += precision.getTimeStep();
            timesFloat[j] = timefloat;
        }
        if (odeSystem.getAvailableParameters().isEmpty()) {
            return new ArrayTrajectory(data, timesFloat, point.getDimension());
        } else {
            return new ArrayTrajectory(point, data, timesFloat, odeSystem.dimension());
        }
    }
}
