package org.sybila.parasim.extension.projectManager.view;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.sybila.parasim.extension.projectManager.api.ExperimentListener;
import org.sybila.parasim.extension.projectManager.api.ProjectManager;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class PhonyProjectManager extends JFrame implements ProjectManager {

    private ExperimentListener experimentListener;

    @Override
    public ExperimentListener getExperimentListener() {
        return experimentListener;
    }

    @Override
    public void setExperimentListener(ExperimentListener target) {
        experimentListener = target;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b == true) {
            JOptionPane.showMessageDialog(getRootPane(), "This project manager roughly shows how a project manager should look like. However, it lacks all functionality.",
                    "This is Only a Test", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                PhonyProjectManager manager = new PhonyProjectManager();
                manager.setVisible(true);
            }
        });
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////// The following pertains to visualisation /////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public PhonyProjectManager() {
        setTitle("Phony Project Manager");
        setSize(1250, 750);
        setLocation(200, 100);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
