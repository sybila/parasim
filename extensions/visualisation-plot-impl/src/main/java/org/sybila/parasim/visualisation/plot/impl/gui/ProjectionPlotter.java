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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.sybila.parasim.model.ode.PointVariableMapping;
import org.sybila.parasim.model.space.OrthogonalSpace;
import org.sybila.parasim.model.verification.result.AbstractVerificationResult;
import org.sybila.parasim.model.verification.result.VerificationResult;
import org.sybila.parasim.visualisation.plot.api.Plotter;
import org.sybila.parasim.visualisation.plot.impl.LayerFactory;
import org.sybila.parasim.visualisation.plot.impl.LayerMetaFactory;
import org.sybila.parasim.visualisation.plot.impl.ResultPlotterConfiguration;

/**
 * Plots a 2D projection of a generally n-D space in a window. Exactly two axes
 * are taken for the 2D space base, the other coordinates are left out. For the
 * points not to overlap, left out axes are divided into intervals which may be
 * chosen using sliders.
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ProjectionPlotter extends JFrame implements Plotter {

    private static final int INSET = 10;
    private static final Insets PADDING = new Insets(INSET, INSET, INSET, INSET);
    //components//
    private JPanel sliders, axes;
    private Canvas canvas;
    private AxisChooser xAxis, yAxis;
    private AxisSlider[] axisSliders;
    //variables//
    private PointVariableMapping names;
    private OrthogonalSpace extent;
    private int dimension;
    private LayerMetaFactory metaLayers;
    private LayerFactory layers;

    /**
     * Creates new plotter on a given verification result with specified axes labels,
     * algorithm of projection into 2D and point appearance.
     *
     * @param result Result of verification. Is not necessarily rendered.
     * @param names Labels of axes.
     * @param pointSource Specifies manner of projection into 2D and contains rendered points.
     * @param pointAppearance Specifies point appearance.
     */
    public ProjectionPlotter(ResultPlotterConfiguration conf, VerificationResult result, PointVariableMapping names, LayerMetaFactory pointSource, PointRenderer pointAppearance) {
        dimension = result.getPoint(0).getDimension();
        this.names = names;
        extent = AbstractVerificationResult.getEncompassingSpace(result);

        init(pointAppearance, conf.getPlotterWindowWidth(), conf.getPlotterWindowHeight());

        metaLayers = pointSource;
        //initially, (0,1) are chosen//
        layers = metaLayers.getLayerFactory(0, 1);
        //updating sliders//
        for (int i = 0; i < dimension; i++) {
            axisSliders[i].update(layers.ticks(i), 0);
        }
        updateView();
    }

    private void init(PointRenderer appearance, int width, int height) {
        ResourceBundle strings = ResourceBundle.getBundle(getClass().getSimpleName());
        setTitle(strings.getString("title"));

        setSize(width, height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        setLayout(new BorderLayout());

        initCanvas(appearance);
        initSliders();
        initAxes(strings.getString("x_axis"), strings.getString("y_axis"));
    }

    private void initCanvas(PointRenderer appearance) {
        canvas = new Canvas(appearance);
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
            axisSliders[i] = new AxisSlider(names.getName(i), changed, extent.getMinBounds().getValue(i), extent.getMaxBounds().getValue(i));
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
     * Called when an axis is selected by AxisChoosers.
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

        float[] values = new float[dimension];
        //get real values from axisSliders//
        for (int i = 0; i < dimension; i++) {
            values[i] = layers.getValue(i, axisSliders[i].getValue());
        }
        //create new LayerFactory//
        layers = metaLayers.getLayerFactory(xSelected, ySelected);
        //update values and maximums of axissliders//
        for (int i = 0; i < dimension; i++) {
            axisSliders[i].update(layers.ticks(i), layers.getTicks(i, values[i]));
        }
        updateView();
    }

    /**
     * Called when an axis slider is moved and view has to be changed
     * accordingly.
     */
    private void updateView() {
        Map<Integer, Integer> projections = new HashMap<Integer, Integer>();
        for (int i = 0; i < dimension; i++) {
            projections.put(i, axisSliders[i].getValue());
        }
        canvas.setPoints(layers.getLayer(projections));
    }

    @Override
    public void plot() {
        setVisible(true);
    }
}
