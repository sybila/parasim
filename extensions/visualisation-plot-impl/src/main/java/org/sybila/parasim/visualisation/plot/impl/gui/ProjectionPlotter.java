/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.visualisation.plot.impl.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.sybila.parasim.model.ode.DoubleMap;
import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.trajectory.ArrayPoint;
import org.sybila.parasim.model.trajectory.Point;
import org.sybila.parasim.model.verification.result.ArrayVerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.Plotter;

/**
 *                    Plots a 2D projection of a generally n-D space in a window. Exactly two axes
 * are taken for the 2D space base, the other coordinates are left out. For the
 * points not to overlap, left out axes are divided into intervals which may be
 * chosen using sliders.
 *
 *                    @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectionPlotter extends JFrame implements Plotter {

    PointVariableMapping names;
    private static final int INSET = 10;
    private static final Insets PADDING = new Insets(INSET, INSET, INSET, INSET);
    JPanel sliders, axes;
    Canvas canvas;
    int dimension;
    AxisChooser xAxis, yAxis;
    AxisSlider[] axisSliders;

    public ProjectionPlotter(VerificationResult result, PointVariableMapping names) {
        dimension = result.getPoint(0).getDimension();
        this.names = names;
        init();
    }

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

        ChangeListener changed = new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent ce) {
                updateView();
            }
        };
        axisSliders = new AxisSlider[dimension];
        for (int i = 0; i < dimension; i++) {
            axisSliders[i] = new AxisSlider(dimension, names.getName(i), changed);
            sliders.add(axisSliders[i]);
        }
        axisSliders[0].setActive(false);
        axisSliders[1].setActive(false);

        add(sliders, BorderLayout.LINE_END);
    }

    private void initAxes(String xAxe, String yAxe) {
        AxisChooser[] axisChoosers = AxisChooser.getPairedAxes(dimension, names, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                updateAxes();
            }
        });
        xAxis = axisChoosers[0];
        yAxis = axisChoosers[1];

        axes = new JPanel();
        axes.setLayout(new BoxLayout(axes, BoxLayout.LINE_AXIS));
        axes.setBorder(new EmptyBorder(PADDING));
        axes.add(new JLabel(xAxe));
        axes.add(Box.createRigidArea(new Dimension(INSET, INSET)));
        axes.add(xAxis);
        axes.add(Box.createRigidArea(new Dimension(2 * INSET, INSET)));
        axes.add(new JLabel(yAxe));
        axes.add(Box.createRigidArea(new Dimension(INSET, INSET)));
        axes.add(yAxis);
        axes.add(Box.createHorizontalGlue());
        add(axes, BorderLayout.PAGE_END);
    }

    /**
     *                  Called when an axis is selected by AxisChoosers.
     */
    private void updateAxes() {
        int xSelected = xAxis.getSelected();
        int ySelected = yAxis.getSelected();

        // change sliders //
        for (AxisSlider as : axisSliders) {
            as.setActive(true);
        }
        axisSliders[xSelected].setActive(false);
        axisSliders[ySelected].setActive(false);


    }

    /**
     *                  Called when an axis slider is moved and view has to be changed
     * accordingly.
     */
    private void updateView() {
    }

    @Override
    public void plot() {
        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                Point[] points = new Point[5];
                points[0] = new ArrayPoint(0, 0f, 2.3f, 0f);
                points[1] = new ArrayPoint(0, 3.5f, 4.3f, 0f);
                points[2] = new ArrayPoint(0, 5.1f, 0.6f, 0f);
                points[3] = new ArrayPoint(0, 4.1f, 1.3f, 1f);
                points[4] = new ArrayPoint(0, 1.8f, 3.8f, 1f);
                float[] robustness = new float[]{-2.5f, 0.8f, 3.4f, 2.9f, -5.3f};
                VerificationResult result = new ArrayVerificationResult(5, points, robustness);

                Plotter test = new ProjectionPlotter(result, new TestVariableMapping());
                test.plot();
            }
        });

    }
}

class TestVariableMapping extends DoubleMap<Integer> implements PointVariableMapping {

    public TestVariableMapping() {
        put(new Integer(0), "x");
        put(new Integer(1), "y");
        put(new Integer(2), "z");
    }

    public int getDimension() {
        return 3;
    }
}
