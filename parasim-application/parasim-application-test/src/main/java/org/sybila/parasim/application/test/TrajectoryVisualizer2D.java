package org.sybila.parasim.application.test;

import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.trajectory.Trajectory;

/**
 * @author <a href="mailto:xpapous1@fi.muni.cz">Jan Papousek</a>
 */
public class TrajectoryVisualizer2D {
    
    private int dimX;
    private int dimY;
    private Grid2D grid;
    private float normalizeX;
    private float normalizeY;
    
    
    public TrajectoryVisualizer2D(Grid2D grid, int dimX, int dimY, float normalizeX, float normalizeY) {
        this.grid = grid;
        this.dimX = dimX;
        this.dimY = dimY;
        this.normalizeX = normalizeX;
        this.normalizeY = normalizeY;
    }
    
    public void printTrajectory(Trajectory trajectory) {
        printFirstPoint(trajectory.getFirstPoint());
        grid.stroke(255, 0, 0);
        Point previous = null;
        for(Point point: trajectory) {
            if (previous != null) {
                printLine(previous, point);
            }
            previous = point;
        }
    }   
    
    private void printFirstPoint(Point point) {
        grid.stroke(0, 255, 0);
        grid.fill(0, 255, 0);
        grid.rect(
            grid.normalizeX(normalizeX * point.getValue(dimX)) - 4,
            grid.normalizeY(normalizeY * point.getValue(dimY)) - 4,
            8,
            8
        );
    }
    
    private void printLine(Point start, Point end) {
        grid.line(
            grid.normalizeX(normalizeX * start.getValue(dimX)),
            grid.normalizeY(normalizeY * start.getValue(dimY)),
            grid.normalizeX(normalizeX * end.getValue(dimX)),
            grid.normalizeY(normalizeY * end.getValue(dimY))
        );
        
    }
}
