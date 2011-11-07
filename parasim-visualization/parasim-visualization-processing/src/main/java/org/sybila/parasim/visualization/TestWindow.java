/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.visualization;

/**
 *
 * @author Asan
 */
public class TestWindow extends javax.swing.JFrame {
    
    TestWindow() {
        Visualization testVisualization = new Visualization();
        setSize(818, 642);          
        getContentPane().add(testVisualization);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        setVisible(true);
    }
    
    public static void main (String [] args) {
        TestWindow testWindow = new TestWindow();
    }
}
