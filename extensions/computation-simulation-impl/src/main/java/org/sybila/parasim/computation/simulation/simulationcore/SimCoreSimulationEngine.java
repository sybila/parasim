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
 * @author <a href="mailto:433392@fi.muni.cz">Vojtech Bruza</a>
 */
public class SimCoreSimulationEngine implements SimulationEngine {

    @Override
    public void close() {
    }

    /**
     * array for caching time of simulations for Parasim
     */
    float[] l_times;
    /**
     * array for caching time of simulations for Simulatin Core Library
     */
    double [] l_timesDouble;
    /**
     * start of last simulation
     */
    float l_startTime;
    /**
     * end of last simulation
     */
    double l_endTime;
    /**
     * time step of previous simulation
     */
    float timeStep;


    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration precision) {
        Model model = odeSystem.getOriginalModel();

//        System.out.println("PARAMETER VALUES: " + odeSystem.getAvailableParameters().keySet() +" VARIABLES: " + odeSystem.getVariables().keySet());
//        // DONE how to recognize if the index is same as in my Model (corresponding parameters and variables)

//        //SETTING VARIABLES
        for(Variable variable : odeSystem.getVariables().values()){
            if (!variable.isSubstituted()) {
                //set species (variables) values in model
                model.getSpecies(variable.getName()).setValue(point.getValue(variable.getIndex()));
            }
        }

//        //odeSystem.getAvailableParameters() returns only parameters, use odeSystem.getVariables() to get variables
//        //DONE findOut if parameters in parasim are also variables (if it is synonym)

//        //SETTING PARAMETERS
//        for(Parameter parameter : odeSystem.getAvailableParameters().values()){
//            if (!parameter.isSubstituted()) {
//                System.out.println(parameter.getName() + " - VALUE: " + point.getValue(parameter.getIndex()));
//                //set parameters values in model
//                model.getParameter(parameter.getName()).setValue(point.getValue(parameter.getIndex()));//what is the difference?? "parameter.evaluate(point)" - doesnt work if parameter is not substituted vs "point.getValue(parameter.getIndex())" - works
//            }
//        }
//        //DONE set parameter value in model according to ode system parameter value
//        //DONE create substituted odeSystem (model)

        //DONE!! SET PARAMETERS
        for(Parameter parameter : odeSystem.getAvailableParameters().values()){
            if (!parameter.isSubstituted()) { //substituted are those that are set at the beginning (not perturbating over them)
                //set parameters values in model
                model.getParameter(parameter.getName()).setValue(point.getValue(parameter.getIndex()));//what is the difference?? "parameter.evaluate(point)" - doesn't work if parameter is not substituted vs "point.getValue(parameter.getIndex())" - works
            }
        }


        //SET SIMULATION TIME (NUM OF ITERATIONS) - START, END, NUM OF ITERATION, TIME STEP
        long numOfIterations = Math.round(Math.ceil((1.05 * timeLimit - point.getTime()) / precision.getTimeStep())); //magical constant 1.05 taken from Jan Papousek's implementation => guessing it is for rendering reasons (to simulate a bit more data than a user expects)
        if (numOfIterations > Integer.MAX_VALUE) { // step limit is max integer value, because it needs to be retyped from long later on
            throw new IllegalStateException("Can't simulate the trajectory because the number of iterations <" + numOfIterations + "> has exceeded its maximum <" + Integer.MAX_VALUE + ">.");
        }

        //TIME - needs to be float due to communication with Parasim and double due to SimCore
        if(l_startTime != point.getTime() || l_endTime != timeLimit || timeStep != precision.getTimeStep()){
            l_startTime = point.getTime();
            l_endTime = timeLimit;
            timeStep = precision.getTimeStep();
            l_times = new float[(int) numOfIterations];
            float timeFloat = point.getTime();
            l_timesDouble = new double[(int) numOfIterations];
            for (int j = 0; j < l_times.length; j++) {
                timeFloat += precision.getTimeStep();
                l_times[j] = timeFloat;
                l_timesDouble[j] = timeFloat;
            }
        }

        //SIMULATION METHOD CHOICE
        SBMLinterpreter interpreter = null;
        try {
            interpreter = new SBMLinterpreter(model);
        } catch (ModelOverdeterminedException e) {
            e.printStackTrace();
        }
        AdaptiveStepsizeIntegrator solver = new RosenbrockSolver();
        MultiTable solution = null;
        //TOLERANCE SETTING
        //relative error
        solver.setRelTol(precision.getMaxRelativeError());
        //absolute error for each variable not supported => using minimum of all variables as the tolerance for each of them
        float maxAbsoluteError = precision.getMaxAbsoluteError(0);
        for (int i = 0; i < odeSystem.getVariables().size(); i++){
            if(precision.getMaxAbsoluteError(i) < maxAbsoluteError){
                maxAbsoluteError = precision.getMaxAbsoluteError(i);
            }
        }
        solver.setAbsTol(maxAbsoluteError);
        //SIMULATION
        try {
            solution = solver.solve(interpreter, interpreter.getInitialValues(), l_timesDouble);
        } catch (DerivativeException e) {
            e.printStackTrace();
        }
        //PARSING DATA TO TRAJECTORY

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
        for (int currentVariable = 0; currentVariable < odeSystem.getVariables().size(); currentVariable++) { //of course simulating only variables, not parameters
            for (int i = 0; i < solution.getRowCount(); i++) {
                simulatedData[currentVariable + i * odeSystem.getVariables().size()] = (float) solution.getColumn(odeSystem.getVariable(currentVariable).getName()).getValue(i);
            }
        }
        //OUTPUT TRAJECTORY
        if (odeSystem.getAvailableParameters().isEmpty()) {
            return new ArrayTrajectory(simulatedData, l_times, point.getDimension());
        } else {
            return new ArrayTrajectory(point, simulatedData, l_times, odeSystem.getVariables().size());
        }
    }
}
