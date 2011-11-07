package org.sybila.parasim.visualization;

/**
 * public methods for user-driven control of visualization
 *  
 * @author <a href="mailto:xstreck1@fi.muni.cz">Adam Streck</a>
 */

public class VisualizationControl  {
    private Visualization visualization;

    /**
     * 
     * @param Visulazation that will be controlle using the controller 
     */
    public VisualizationControl(Visualization controlled) {
        visualization = controlled;
    }
    
    /**
     * 
     * @return controlled visualization 
     */
    public Visualization getVisualization() {
        return visualization;
    }
    
    public void stopDrawing() {
        visualization.noLoop();
    }
    
    public void continueDrawing() {
        visualization.loop();
    }  
    
    public void startNewSimulation(int number, int dimensions, int times)  {
        visualization.mySimulation = new FakeSimulation(number, dimensions, times);
        visualization.setLayout();
        System.out.println("New simulation started with" + Math.pow(number, dimensions) + " trajectories in "+ dimensions + " dimensions, each with " + number + " points."); 
    }    
}
