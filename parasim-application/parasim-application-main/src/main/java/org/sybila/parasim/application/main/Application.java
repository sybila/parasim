package org.sybila.parasim.application.main;

import org.sybila.parasim.visualization.*;

/**
 * Main running class.
 *
 * @author <a href="mailto:xstreck1@fi.muni.cz">Adam Streck</a>
 */

public class Application {
    private Window mainWindow;
    private ParallelVisualization dataVisualization;
    private VisualizationControl controller;
    
    public static void main (String [] args) {
        Application mainApplication = new Application();
        // Create and display new window
        
    }
    
    /**
     * Creates an application - mainly the window and sets up the visualization.
     * Visualization is held in a content pane, whose size as well as size of the visualization are now hard-wired to 800*600.
     */
    Application () {
        dataVisualization = new ParallelVisualization(800,600);
        controller = new VisualizationControl(dataVisualization);      
        mainWindow = new Window();
        
        mainWindow.setController(controller);
        
        dataVisualization.init();
        mainWindow.setVisible(true);
    }
    
    /** 
     * 
     * @return VisualizationControl for current visualization.
     */
    public VisualizationControl getController() {
        return controller;
    }
}
