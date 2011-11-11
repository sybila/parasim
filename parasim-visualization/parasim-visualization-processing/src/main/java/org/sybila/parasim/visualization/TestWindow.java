/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sybila.parasim.visualization;

/**
 * Simple class for faster testing.
 * @author Asan
 */
public class TestWindow extends javax.swing.JFrame {
    
    TestWindow() {
        ParallelVisualization testVisualization = new ParallelVisualization(800,600);
        setSize(818, 642);          
        getContentPane().add(testVisualization);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        testVisualization.mySimulation = new FakeSimulation(3, 4, 10);
        testVisualization.setLayout();
        
        setVisible(true);
    }
    
    public static void main (String [] args) {
        TestWindow testWindow = new TestWindow();
    }
}
