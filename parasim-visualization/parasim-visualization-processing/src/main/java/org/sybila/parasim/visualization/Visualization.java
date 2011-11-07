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
    private int visWidth; // Width of the whole visualization
    private int visHeight; // Height of the whole visualization
    private int dimensions; // Number of dimensions to display
    private int displayHeight; // Height of output from a single dimension
    private int displayWidth; // Width of output from a single dimension
    private final int borderWidth = 5; // Width of the space between output and display frame
    
    // Values used for determining the size of each window
    private float maxTime;
    private float [] maxValues;
    
    private final int borderCol = 0xFF000000;
    private final int fillCol = 0xFFAAAAAA;
    private final int backCol = 0xFFFFFFFF;
    private final int trajCol = 0x33FF0000;
    
    FakeSimulation mySimulation;
    
    public Visualization () {
        visWidth = 800;
        visHeight = 600;
        init();
    }
    
    /**
     * Function is called first during the init() of the applet.
     * Creates a window of requested size and sets up properties of animation. 
     */
    @Override
    public void setup () { 
        size(visWidth, visHeight);
        frameRate(2);           
    }
    
    public void setLayout() {
        try {
        if (mySimulation == null)
            throw new IllegalAccessException ("Trying to layout a not initialized simulation.");
        }
        catch (IllegalAccessException error) {
            System.out.println(error.getMessage());
            error.printStackTrace();
        }
        
        dimensions = mySimulation.getDataBlock().getTrajectory(0).getDimension();
        maxValues = new float [dimensions];
        displayHeight = height / dimensions;
        displayWidth = width;
    }
    /**
     * For every dimension obtains maximal value and from all the trajectories 
     * and maximal time. 
     */
    private void getBoundaries() {
        Iterator trajectoryIt = mySimulation.getDataBlock().iterator();
        Iterator pointIt;
        Trajectory trajectory;
        Point point;
        
        // Go through all the trajectories
        while (trajectoryIt.hasNext()) {
            trajectory = (AbstractTrajectory) trajectoryIt.next();
            pointIt = trajectory.iterator();
            
            // For each point in trajectory check, if its value is not greater than currnet maximal value
            for (int p = 0; p < trajectory.getLength(); p++)  {
               point = (Point) trajectory.getPoint(p);
               for (int i = 0; i < dimensions; i++) {
                   point.getValue(i);
                   if (maxValues[i] < point.getValue(i))
                       maxValues[i] = point.getValue(i);
               }
            }
            maxTime = max(maxTime, trajectory.getLastPoint().getTime());
        }            
    }
    
    
    /**
     * Redraws the screen.
     */
    
    @Override
    public void draw () { 
        if (mySimulation != null) {
            mySimulation.AddPoints(3);
            getBoundaries();
        }

        background(255);
        fill(fillCol);

        for (int i = 0; i < dimensions; i++) {
            stroke(borderCol);
            rect (borderWidth, borderWidth + displayHeight*i, 
                  displayWidth - borderWidth*2, displayHeight - borderWidth*2); 
            stroke(trajCol);
            if (mySimulation != null)
                drawDimension(i);
        }
    }    
    
    /*
     * Draws a single dimension of parallel data.
     */
    private void drawDimension(int dimNum) {  
        int yBottom = displayHeight*(dimNum + 1)- borderWidth;
        float valCorrection = (float) (displayHeight - 2*borderWidth) / (float) maxValues[dimNum];
        float timeCorrection = (float) (displayWidth - 2*borderWidth) / (float) maxTime ;
        
        Iterator trajectoryIt = mySimulation.getDataBlock().iterator();
        Iterator pointIt;
        AbstractTrajectory trajectory;
        Point oldPoint;
        Point point;
        
        while (trajectoryIt.hasNext()) {
            trajectory = (AbstractTrajectory) trajectoryIt.next();
            pointIt = trajectory.iterator();
            
            if (trajectory.hasPoint(0))
                oldPoint = (Point) trajectory.getPoint(0);
            else
                break;
            
            for (int p = 1; p < trajectory.getLength(); p++)  {
                point = (Point) trajectory.getPoint(p);
                int start_X = (int) (((float)oldPoint.getTime())*timeCorrection + borderWidth);
                int start_Y = (int) min((- ((float)oldPoint.getValue(dimNum))*valCorrection + yBottom), yBottom);
                int end_X = (int) (((float)point.getTime())*timeCorrection + borderWidth);
                int end_Y = (int) min((- ((float)point.getValue(dimNum))*valCorrection  + yBottom),yBottom);
                line(start_X, start_Y, end_X, end_Y);
                oldPoint = point;
            }
        }  
    } 
}
