package org.sybila.parasim.computation.simulation.cpu;

import dk.ange.octave.OctaveEngine;
import dk.ange.octave.OctaveEngineFactory;
import dk.ange.octave.type.OctaveDouble;
import java.util.Arrays;
import org.sybila.parasim.computation.simulation.AdaptiveStepConfiguration;
import org.sybila.parasim.computation.simulation.ArraySimulatedDataBlock;
import org.sybila.parasim.computation.simulation.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.Simulator;
import org.sybila.parasim.computation.simulation.Status;
import org.sybila.parasim.model.ode.OctaveOdeSystem;
import org.sybila.parasim.model.trajectory.ArrayDataBlock;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class OctaveSimulator implements Simulator<AdaptiveStepConfiguration, SimulatedDataBlock<Trajectory>> {
   
    public SimulatedDataBlock<Trajectory> simulate(AdaptiveStepConfiguration configuration, DataBlock<Trajectory> data) {
        OctaveEngine octave = new OctaveEngineFactory().getScriptEngine();
        OctaveOdeSystem octaveOdeSystem = new OctaveOdeSystem(configuration.getOdeSystem().encoding());
        octave.eval(octaveOdeSystem.octaveString());
        octave.eval("lsode_options(\"step limit\", " + configuration.getMaxNumberOfIterations() + ");");
        if (configuration.getMaxRelativeError() > 0) {
            octave.eval("lsode_options(\"relative tolerance\", " + configuration.getMaxRelativeError()+ ");");    
        }
        Trajectory[] trajectories = new Trajectory[data.size()];
        Status[] statuses = new Status[data.size()];
        for (int i = 0; i < data.size(); i++) {
            trajectories[i] = simulateTrajectory(octave, octaveOdeSystem, configuration, data.getTrajectory(i).getLastPoint());
            if (trajectories[i].getLastPoint().getTime() < configuration.getTargetTime()) {
                statuses[i] = Status.TIMEOUT;
            }
            else {
                statuses[i] = Status.OK;
            }
        }
        octave.close();
        return new ArraySimulatedDataBlock(new ArrayDataBlock(trajectories), statuses);
    }   
    
    private Trajectory simulateTrajectory(OctaveEngine octave, OctaveOdeSystem octaveOdeSystem, AdaptiveStepConfiguration configuration, Point initialPoint) {
        octave.eval("i = " + Arrays.toString(initialPoint.toArray()) + ";");
        octave.eval("t = linspace(" + initialPoint.getTime() + ", " + configuration.getTargetTime() + ", " + Math.min(Math.round((configuration.getTargetTime() - initialPoint.getTime()) / configuration.getTimeStep()), configuration.getMaxNumberOfIterations()) + ")");
        System.out.println("i = " + Arrays.toString(initialPoint.toArray()) + ";");
        System.out.println(octaveOdeSystem.octaveString());
        System.out.println("t = linspace(" + initialPoint.getTime() + ", " + configuration.getTargetTime() + ", " + Math.min(Math.round((configuration.getTargetTime() - initialPoint.getTime()) / configuration.getTimeStep()), configuration.getMaxNumberOfIterations()) + ")");
        System.out.println("y = lsode(\"" + octaveOdeSystem.octaveName() + "\", i, t)");
        
        octave.eval("y = lsode(\"" + octaveOdeSystem.octaveName() + "\", i, t)");
        OctaveDouble y = octave.get(OctaveDouble.class, "y");
        double[] loadedData = y.getData();
        float[] data = new float[loadedData.length];
        for (int dim=0; dim<initialPoint.getDimension(); dim++) {
            for(int i=0; i<loadedData.length/initialPoint.getDimension(); i++) {
                data[dim + i * initialPoint.getDimension()] = (float) loadedData[dim * (loadedData.length/initialPoint.getDimension()) + i];
            }
        }
        float[] times = new float[loadedData.length / initialPoint.getDimension()];
        float time = initialPoint.getTime();
        for(int i=0; i<times.length; i++) {
            time += configuration.getTimeStep();
            times[i] = time;
        }
        return new ArrayTrajectory(data, times, initialPoint.getDimension());
    }
    
    public static boolean isAvailable() {
        try {
            new OctaveEngineFactory().getScriptEngine();
            return true;
        }
        catch(Exception ignored) {
            return false;
        }
    }


    
}