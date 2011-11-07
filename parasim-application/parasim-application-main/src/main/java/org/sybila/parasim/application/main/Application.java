package org.sybila.parasim.application.main;

import org.sybila.parasim.visualization.*;

/**
 * Main running class.
 *
 * @author <a href="mailto:xstreck1@fi.muni.cz">Adam Streck</a>
 */

public class Application {
    private Window mainWindow;
    private Visualization dataVisualization;
    private VisualizationControl controller;
    
    public static void main (String [] args) {
        Application mainApplication = new Application();
        // Create and display new window
        
    }
    
    Application () {
        dataVisualization = new Visualization();
        controller = new VisualizationControl(dataVisualization);      
        mainWindow = new Window();
        
        mainWindow.setVisualization(dataVisualization);
        mainWindow.setController(controller);
        
        dataVisualization.init();
        mainWindow.setVisible(true);
    }
            
    public VisualizationControl getController() {
        return controller;
    }
}
