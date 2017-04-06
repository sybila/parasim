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

    /*
    private List<ParameterValue> loadParameterValues(Point point, OdeSystem odeSystem) {
        List<ParameterValue> paramValues = new ArrayList<>();
        for (Parameter param : odeSystem.getAvailableParameters().values()) {
            if (!param.isSubstituted()) {
                paramValues.add(new ParameterValue(param, point.getValue(param.getIndex())));
            }
        }
        return paramValues;
    }
    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration precision) {
        // load parameter values
        List<ParameterValue> paramValues = loadParameterValues(point, odeSystem);
        // create substituted octave ode system //Vojta with values loaded from current point
        OctaveOdeSystem octaveOdeSystem = paramValues.isEmpty() ? new OctaveOdeSystem(odeSystem) : new OctaveOdeSystem(odeSystem.substitute(paramValues));
        // compute
        long numOfIterations = Math.round(Math.ceil((1.05 * timeLimit - point.getTime()) / precision.getTimeStep()));
        if (numOfIterations > getStepLimit()) {
            throw new IllegalStateException("Can't simulate the trajectory because the number of iterations <" + numOfIterations + "> is higher than the given limit <" + getStepLimit() + ">.");
        }
        double[] loadedData = rawSimulation(point, octaveOdeSystem, numOfIterations, precision).getData();
        float[] data = new float[loadedData.length];
        for (int dim = 0; dim < octaveOdeSystem.dimension(); dim++) {
            for (int i = 0; i < loadedData.length / octaveOdeSystem.dimension(); i++) {
                data[dim + i * octaveOdeSystem.dimension()] = (float) loadedData[dim * (loadedData.length / octaveOdeSystem.dimension()) + i];
            }
        }
        float[] times = new float[loadedData.length / octaveOdeSystem.dimension()];
        float time = point.getTime();
        for (int i = 0; i < times.length; i++) {
            time += precision.getTimeStep();
            times[i] = time;
        }
        if (paramValues.isEmpty()) {
            return new ArrayTrajectory(data, times, point.getDimension());
        } else {
            return new ArrayTrajectory(point, data, times, octaveOdeSystem.dimension());
        }
    }
     */

    @Override
    public Trajectory simulate(Point point, OdeSystem odeSystem, double timeLimit, PrecisionConfiguration configuration) {
        Model model = odeSystem.getOriginalModel();

        //DONE how to set model parameters and species values correctly according to current point
        List<ParameterValue> paramValues = new ArrayList<>();
        for (Parameter param : odeSystem.getAvailableParameters().values()) { //for each parameter in odesystem
            if (!param.isSubstituted()) { //if does not have constant value
                paramValues.add(new ParameterValue(param, point.getValue(param.getIndex()))); //add the value from current point to parameter values
            }
        }
        // TODO Vojta - how to recognize if the index is same as in my Model (corresponding parameters and variables)

        //TODO create substituted odeSystem (model)

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
