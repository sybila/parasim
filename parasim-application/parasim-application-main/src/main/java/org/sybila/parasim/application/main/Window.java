
package org.sybila.parasim.application.main;

import org.sybila.parasim.visualization.*;
import java.awt.*;
import javax.swing.*;

/**
 * Main class of GUI - holds interface and visualization.
 *
 * @author <a href="mailto:xstreck1@fi.muni.cz">Adam Streck</a>
 */

class Window extends JFrame {  
    private Container windowCont;
    private BorderLayout windowLayout ;
    private JPanel buttons;
    private JPanel visualizationCont;
    
    private final Color bgColor = Color.WHITE;
    
    public Window() {  
        initWindow();
        createButtons();
        createVisualization();
        
        setVisible(true);     
    }  
    
    private void initWindow() {
        setTitle("Parasim");  
        setSize(1000, 650);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        
        windowCont = getContentPane();
        windowCont.setLayout(null);
        windowCont.setBackground(bgColor);
        
        // windowLayout = new BorderLayout();
        
        // windowCont.setLayout(windowLayout);

    }
    
    private void createButtons() {        
        JButton newSimulationBtn = new JButton("New simulation");
        JButton pauseSimulationBtn = new JButton("Pause");
        JButton continueSimulationBtn = new JButton("Continue");
        
        buttons = new JPanel(new FlowLayout());
        buttons.setBounds(0, 0, 150, 150);
        
        buttons.add(newSimulationBtn);
        buttons.add(continueSimulationBtn);
        buttons.add(pauseSimulationBtn); 
        buttons.setBackground(bgColor);
        
        windowCont.add(buttons);
    }
    
    private void createVisualization(){
        Visualization testVisualization = new Visualization();
        visualizationCont = new JPanel(new FlowLayout());
        visualizationCont.setBounds(150, 0, 850, 800);
        visualizationCont.setBackground(bgColor);

        testVisualization.init();        
        
        visualizationCont.add(testVisualization);
        
        windowCont.add(visualizationCont);  
    }
 }  