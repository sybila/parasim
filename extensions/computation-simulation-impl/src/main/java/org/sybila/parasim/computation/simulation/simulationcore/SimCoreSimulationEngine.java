package org.sybila.parasim.computation.simulation.simulationcore;

import org.sbml.jsbml.Model;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.computation.simulation.cpu.SimulationEngine;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.*;

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
//        Model model = odeSystem.getOriginalModel();
        Model model = null;
        //TODO how to set model parameters and species values correctly according to current point

//        SBMLinterpreter interpreter = null;
//        try {
//            interpreter = new SBMLinterpreter(model);
//        } catch (ModelOverdeterminedException e) {
//            e.printStackTrace();
//        }
//
//        AbstractDESSolver solver = new RosenbrockSolver();
//        double[] timePoints = {0,1,2,3,4,5,6,7,8,9,10}; //TODO Vojta - where to get time array
//        MultiTable solution = null;
//        try {
//            solution = solver.solve(interpreter, interpreter.getInitialValues(), timePoints);
//        } catch (DerivativeException e) {
//            e.printStackTrace();
//        }

        //TODO Vojta - how to create new trajectory
        return null;
    }
}
