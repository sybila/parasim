package org.sybila.parasim.application.test;

import org.sybila.parasim.computation.density.spawn.TrajectorySpawner;
import org.sybila.parasim.computation.density.spawn.cpu.OneTrajectorySpawner;
import org.sybila.parasim.computation.simulation.cpu.Rkf45Simulator;
import org.sybila.parasim.model.trajectory.ListMutableDataBlock;
import org.sybila.parasim.model.trajectory.MutableDataBlock;
import org.sybila.parasim.support.computation.simulation.LotkaVolteraAdaptiveStepConfiguration;
import org.sybila.parasim.support.model.ode.LotkaVolteraInitialSpace;
import processing.core.PApplet;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Main extends Grid2D {

    private static int WINDOW_WIDTH = 800;
    private static int WINDOW_HEIGHT = 640;
    private static int ZERO_X = 50;
    private static int ZERO_Y = 600;
    
    private MutableDataBlock trajectories;
    private DataBlockVisualizer2D visualizer;
    private Rkf45Simulator simulator;
    
    public static void main(String[] args) {
        setupGrid(WINDOW_WIDTH, WINDOW_HEIGHT, ZERO_X, ZERO_Y);
        PApplet.main(new String[] { Main.class.getName() });
    }
    
    @Override
    public void setup() {
        super.setup();
        TrajectorySpawner spawner = new OneTrajectorySpawner();
        trajectories = new ListMutableDataBlock(spawner.spawn(new LotkaVolteraInitialSpace(), 4, 4));
        simulator = new Rkf45Simulator();
        trajectories.append(simulator.simulate(new LotkaVolteraAdaptiveStepConfiguration(), trajectories));
        visualizer = new DataBlockVisualizer2D(trajectories, this, 0, 1, 20, 13);
    }

    @Override
    public void draw() {
        visualizer.printNext();
    }
    
}
