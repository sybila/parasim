package org.sybila.parasim.application.test;

import java.util.ArrayList;
import java.util.List;
import org.sybila.parasim.model.trajectory.DataBlock;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class DataBlockVisualizer2D {
    
    private List<TrajectoryVisualizer2D> visalizers = new ArrayList<TrajectoryVisualizer2D>();
    
    public DataBlockVisualizer2D(DataBlock<Trajectory> dataBlock, Grid2D grid, int dimX, int dimY, float normalizeX, float normalizeY) {
        for(Trajectory trajectory: dataBlock) {
            visalizers.add(new TrajectoryVisualizer2D(trajectory, grid, dimX, dimY, normalizeX, normalizeY));
        }
    }
    
    public void printNext() {
        for(TrajectoryVisualizer2D v: visalizers) {
            v.printNextPoint();
        }
    }
   
}
