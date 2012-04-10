package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.sybila.parasim.visualisation.plot.api.Plotter;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectionPlotter extends JFrame implements Plotter {

    private static final int INSET = 10;
    private static final Insets PADDING = new Insets(INSET, INSET, INSET, INSET);
    JPanel sliders, axes;
    Canvas canvas;

    private void init() {
        ResourceBundle strings = ResourceBundle.getBundle(getClass().getSimpleName());
        setTitle(strings.getString("title"));

        setSize(500, 250); //TODO move to configuration
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setLayout(new BorderLayout());

        initCanvas();
        initSliders();
        initAxes(strings.getString("x_axis"), strings.getString("y_axis"));
    }

    private void initCanvas() {
        canvas = new Canvas();
        JPanel canvasPanel = new JPanel(new BorderLayout());
        canvasPanel.setBorder(new EmptyBorder(PADDING));
        canvasPanel.add(canvas, BorderLayout.CENTER);
        add(canvasPanel, BorderLayout.CENTER);
    }

    private void initSliders() {
        sliders = new JPanel();
        sliders.setLayout(new BoxLayout(sliders, BoxLayout.LINE_AXIS));
        sliders.setBorder(new EmptyBorder(PADDING));
        //TODO individual sliders
        add(sliders, BorderLayout.LINE_END);
    }

    private void initAxes(String xAxe, String yAxe) {
        axes = new JPanel();
        axes.setLayout(new BoxLayout(axes, BoxLayout.LINE_AXIS));
        axes.setBorder(new EmptyBorder(PADDING));
        axes.add(new JLabel(xAxe));
        //TODO combo box
        axes.add(Box.createRigidArea(new Dimension(INSET, INSET)));
        axes.add(new JLabel(yAxe));
        //TODO combo box
        add(axes, BorderLayout.PAGE_END);
    }

    public ProjectionPlotter() {
        init();
    }

    @Override
    public void plot() {
        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                Plotter test = new ProjectionPlotter();
                test.plot();
            }
        });

    }
}
