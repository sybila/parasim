package org.sybila.parasim.application.main;

import org.sybila.parasim.visualization.*;

/**
 * Swing window, connected with a Swing frame - buttons use controller provided by the application.
 * 
 * @author <a href="mailto:xstreck1@fi.muni.cz">Adam Streck</a>
 */
public class Window extends javax.swing.JFrame {
    private VisualizationControl controller;
    
    /** 
     * Create new window and paint it white
     */
    public Window() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(255, 255, 255)); // Doesn't work on the frame itself...
    }
        
    /**
     * Sets the visualization that will be displayed and provides a controller for it
     * 
     * @param controller VisualizationControl for the current visualiztion 
     */
    public void setController(VisualizationControl _controller) {
        controller = _controller;
        visualisationCont.add(controller.getVisualization());
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        visualisationCont = new javax.swing.JPanel();
        newSimulationButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        continueButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        visualisationCont.setBackground(new java.awt.Color(255, 255, 255));
        visualisationCont.setMaximumSize(new java.awt.Dimension(800, 600));
        visualisationCont.setMinimumSize(new java.awt.Dimension(800, 600));
        visualisationCont.setPreferredSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout visualisationContLayout = new javax.swing.GroupLayout(visualisationCont);
        visualisationCont.setLayout(visualisationContLayout);
        visualisationContLayout.setHorizontalGroup(
            visualisationContLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        visualisationContLayout.setVerticalGroup(
            visualisationContLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        newSimulationButton.setText("New Simulation");
        newSimulationButton.setActionCommand("New simulation");
        newSimulationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSimulationButtonActionPerformed(evt);
            }
        });

        pauseButton.setText("Pause");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        continueButton.setText("Continue");
        continueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(newSimulationButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pauseButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(continueButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(visualisationCont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newSimulationButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pauseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(continueButton))
            .addComponent(visualisationCont, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void newSimulationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSimulationButtonActionPerformed
        controller.startNewSimulation(4, 4, 10);
    }//GEN-LAST:event_newSimulationButtonActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        controller.stopDrawing();
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
        controller.continueDrawing();
    }//GEN-LAST:event_continueButtonActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton continueButton;
    private javax.swing.JButton newSimulationButton;
    private javax.swing.JButton pauseButton;
    private javax.swing.JPanel visualisationCont;
    // End of variables declaration//GEN-END:variables
}
