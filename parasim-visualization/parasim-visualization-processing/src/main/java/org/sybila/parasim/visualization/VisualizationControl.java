package org.sybila.parasim.visualization;

/**
 * public methods for user-driven control of visualization
 *  
 * @author <a href="mailto:xstreck1@fi.muni.cz">Adam Streck</a>
 */

public class VisualizationControl  {
    private ParallelVisualization visualization;

    /**
     * 
     * @param Visulazation that will be controlle using the controller 
     */
    public VisualizationControl(ParallelVisualization controlled) {
        visualization = controlled;
    }
    
    /**
     * 
     * @return controlled visualization 
     */
    public ParallelVisualization getVisualization() {
        return visualization;
    }
    
    /**
     * Stops generating next points (and redrawing)
     */
    public void stopDrawing() {
        visualization.noLoop();
    }
    
    /**
     * Resumes generating next points (and redrawing), if stopped.
     */
    public void continueDrawing() {
        visualization.loop();
    }  
    
    /**
     * Loses old data and starts new simulation.
     * 
     * @param number - number of start points in single dimension
     * @param dimensions - number of dimensions
     * @param times - number of points generated for each trajectory
     */
    public void startNewSimulation(int number, int dimensions, int times)  {
        visualization.mySimulation = new FakeSimulation(number, dimensions, times);
        visualization.setLayout();
        System.out.println("New simulation started with " + (int) Math.pow(number, dimensions) + " trajectories in "+ dimensions + " dimensions, each with " + number + " points."); 
    }    
}
