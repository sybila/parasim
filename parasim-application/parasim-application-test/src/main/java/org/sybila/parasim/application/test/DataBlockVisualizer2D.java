package org.sybila.parasim.application.test;

import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DataBlockVisualizer2D {
    
    private TrajectoryVisualizer2D trajectoryVisualizer;
    
    public DataBlockVisualizer2D(Grid2D grid, int dimX, int dimY, float normalizeX, float normalizeY) {
        trajectoryVisualizer = new TrajectoryVisualizer2D(grid, dimX, dimY, normalizeX, normalizeY);
    }
    
    public void printDataBlock(DataBlock<Trajectory> trajectories) {
        for(Trajectory trajectory: trajectories) {
            trajectoryVisualizer.printTrajectory(trajectory);
        }

    }
   
}
