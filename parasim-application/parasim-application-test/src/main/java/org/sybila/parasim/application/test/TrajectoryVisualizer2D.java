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
    private Trajectory trajectory;
    private int lastPointIndex = -1;
    
    public TrajectoryVisualizer2D(Trajectory trajectory, Grid2D grid, int dimX, int dimY, float normalizeX, float normalizeY) {
        this.grid = grid;
        this.dimX = dimX;
        this.dimY = dimY;
        this.normalizeX = normalizeX;
        this.normalizeY = normalizeY;
        this.trajectory = trajectory;
    }
    
    public void printNextPoint() {
        if (lastPointIndex >= trajectory.getLength() - 1) {
            return;
        }
        if (lastPointIndex != -1) {
//            printFirstPoint(trajectory.getPoint(lastPointIndex), 255, 255, 255);
//            printFirstPoint(trajectory.getPoint(lastPointIndex+1), 0, 255, 0);
            printLine(trajectory.getPoint(lastPointIndex), trajectory.getPoint(lastPointIndex+1));
        }
        lastPointIndex++;
    }   
    
    private void printFirstPoint(Point point, float r, float g, float b) {
        grid.stroke(r, g, b);
        grid.fill(r, g, b);
        grid.rect(
            grid.normalizeX(normalizeX * point.getValue(dimX)) - 4,
            grid.normalizeY(normalizeY * point.getValue(dimY)) - 4,
            8,
            8
        );
    }
    
    private void printLine(Point start, Point end) {
        grid.stroke(255, 0, 0);
        grid.line(
            grid.normalizeX(normalizeX * start.getValue(dimX)),
            grid.normalizeY(normalizeY * start.getValue(dimY)),
            grid.normalizeX(normalizeX * end.getValue(dimX)),
            grid.normalizeY(normalizeY * end.getValue(dimY))
        );
        
    }
}
