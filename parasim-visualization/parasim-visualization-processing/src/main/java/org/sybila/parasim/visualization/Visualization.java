package org.sybila.parasim.visualization;

import java.util.Iterator;
import org.sybila.parasim.model.trajectory.*;
import processing.core.*;

/**
 * Class crates applet with visualization data.
 * Data are redrawn every period 30 times per second.
 *  
 * @author <a href="mailto:xstreck1@fi.muni.cz">Adam Streck</a>
 */
public class Visualization extends PApplet{
    private int visWidth;
    private int visHeight;
    private int dimensions;
    private int displayHeight;
    private int displayWidth;  
    private final int border_width = 5;
    
    private float maxTime;
    private float [] maxValues;
    
    private final int borderCol = 0xFF000000;
    private final int fillCol = 0xFFAAAAAA;
    private final int backCol = 0xFFFFFFFF;
    
    FakeSimulation mySimulation;
    
    public Visualization () {
        visWidth = 800;
        visHeight = 600;
        
        mySimulation = new FakeSimulation(10, 4, 100);
    }
    
    /**
     * Function is called first during the init() of the applet.
     * Creates a window of requested size and sets up properties of animation. 
     */
    public void setup () { 
        size(visWidth, visHeight);
        dimensions = mySimulation.getDataBlock().getTrajectory(0).getDimension();
        displayHeight = height / dimensions;
        displayWidth = width;
        
        fill(fillCol);
        stroke(borderCol);
        
        getBoundaries(); 
    }
    
    /**
     * For every dimension obtains maximal value and from all the trajectories 
     * and maximal time. 
     */
    private void getBoundaries() {
        Iterator trajectoryIt = mySimulation.getDataBlock().iterator();
        Iterator pointIt;
        AbstractTrajectory trajectory;
        Point point;
        
        while (trajectoryIt.hasNext()) {
            trajectory = (AbstractTrajectory) trajectoryIt.next();
            pointIt = trajectory.iterator();
            while (pointIt.hasNext()) {
               point = (Point) pointIt.next();
               for (int i = 0; i < dimensions; i++) {
                   maxValues[i] = max(maxValues[i], point.getValue(i));
               }
            }
            maxTime = max(maxTime, trajectory.getLastPoint().getTime());
        }            
    }
    
    
    /**
     * Redraws the screen.
     */
    public void draw () { 
       background(255);

       for (int i = 0; i < dimensions; i++) {
           rect (border_width, border_width + displayHeight*i, 
                 displayWidth - border_width*2, displayHeight - border_width*2);
           drawDimension(i);
       }     
    }    
    
    /*
     * Draws a single dimension of parallel data.
     */
    private void drawDimension(int dimNum) {   
        Iterator trajectory = mySimulation.getDataBlock().iterator();
    } 
}
