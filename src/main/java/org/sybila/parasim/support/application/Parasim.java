package org.sybila.parasim.support.application;

import org.sybila.parasim.computation.simulation.SimulatedDataBlock;
import org.sybila.parasim.computation.simulation.cpu.Rkf45Simulator;
import org.sybila.parasim.model.trajectory.Trajectory;
import org.sybila.parasim.support.computation.simulation.LotkaVolteraAdaptiveStepConfiguration;
import org.sybila.parasim.support.computation.simulation.LotkaVolteraInitialDataBlock;
import org.sybila.parasim.support.visualization.DataBlockVisualizer2D;
import org.sybila.parasim.support.visualization.Grid2D;
import processing.core.PApplet;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class Parasim extends Grid2D {

    private static int WINDOW_WIDTH = 800;
    private static int WINDOW_HEIGHT = 640;
    private static int ZERO_X = 50;
    private static int ZERO_Y = 600;
    
    public static void main(String[] args) {
        setupGrid(WINDOW_WIDTH, WINDOW_HEIGHT, ZERO_X, ZERO_Y);
        PApplet.main(new String[] { "org.sybila.parasim.support.application.Parasim" });
    }
    
    @Override
    public void setup() {
        super.setup();
        SimulatedDataBlock<Trajectory> result = new Rkf45Simulator().simulate(new LotkaVolteraAdaptiveStepConfiguration(), new LotkaVolteraInitialDataBlock());
        new DataBlockVisualizer2D(this, 0, 1, 20, 13).printDataBlock(result);
    }

}
