/**
 * Copyright 2011-2018, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.application.model;

import com.github.ggalmazor.ltdownsampling.LTThreeBuckets;
import com.github.ggalmazor.ltdownsampling.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.sybila.parasim.computation.verification.api.Monitor;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.model.trajectory.ArrayTrajectory;
import org.sybila.parasim.model.verification.Robustness;

import javax.swing.*;

import static java.lang.Math.min;
import static javax.swing.WindowConstants.HIDE_ON_CLOSE;

/**
 * @author <a href="mailto:pejznoch@mail.muni.cz>Ales Pejznoch</a>
 */
public class ResultUtils {

    private ResultUtils() {
    }

    public static void plotGraphs(String title, Monitor monitor, ArrayTrajectory trajectory, OdeSystem odeSystem) {
        try {
            Toolkit xToolkit = Toolkit.getDefaultToolkit();
            java.lang.reflect.Field awtAppClassNameField =
                    xToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(xToolkit, "parasimGraph");
        } catch (final Exception e) {
            // we can ignore failed changing of window class name
        }

        List<Monitor> monitors = new ArrayList<>();
        monitors.add(monitor);
        for (int i=0; i<monitors.size(); i++) {
            monitors.addAll(monitors.get(i).getSubmonitors());
        }

        int graphCount = monitors.size() + 1;
        int windowWidth = 960;
        int windowHeight = 600;
        int graphWidth;
        int graphHeight;
        int rowCount;
        int columnCount;

        if (graphCount % 2 == 1) {
            // odd
            rowCount = 1;
            columnCount = graphCount;
            graphHeight = windowHeight;
            graphWidth = windowWidth / graphCount;
        } else {
            // even
            rowCount = graphCount / 2;
            columnCount = graphCount / rowCount;
            graphHeight = windowHeight / rowCount;
            graphWidth = windowWidth / columnCount;
        }
        if (graphCount>6) {
            graphWidth = windowWidth / 2;
            graphHeight = windowHeight / 2;
        }

        List<XYChart> charts = new ArrayList<>();

        charts.add(buildTrajectoryChart(trajectory, odeSystem, graphWidth, graphHeight));

        for (Monitor m: monitors) {
            charts.add(buildRobustnessGraph(m, graphWidth, graphHeight));
        }

        // customize Charts
        for (XYChart chart: charts) {
            chart.getStyler().setPlotGridLinesVisible(true);
            chart.getStyler().setXAxisTickMarkSpacingHint(100);
            chart.getStyler().setToolTipsEnabled(true);
            chart.getStyler().setYAxisGroupPosition(1, Styler.YAxisPosition.Right);
            chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
            Color c = new Color(200,200,200,70);
            chart.getStyler().setLegendBackgroundColor(c);
        }

        JFrame frame = new JFrame("Analysis");

        JPanel panel = new JPanel();
        JLabel label = new JLabel("Analysis of " + title);
        Font labelFont = label.getFont();
        label.setFont(new Font(labelFont.getName(), Font.BOLD, 20));
        panel.add(label);

        JPanel mainPanel = new JPanel(new GridLayout(rowCount, columnCount));

        // populate mainPanel with graphs
        for (XYChart chart : charts) {
            if (chart != null) {
                XChartPanel<XYChart> chartPanel = new XChartPanel<>(chart);
                mainPanel.add(chartPanel);
            } else {
                JPanel chartPanel = new JPanel();
                mainPanel.add(chartPanel);
            }
        }
        JPanel all = new JPanel(new BorderLayout());


        all.add(panel, BorderLayout.PAGE_START);
        all.add(mainPanel, BorderLayout.CENTER);
        JScrollPane scrPane = new JScrollPane(all);
        frame.add(scrPane);

        // windows size + scrollbar + header
        scrPane.setPreferredSize(new Dimension(windowWidth+20, windowHeight+40));
        scrPane.getVerticalScrollBar().setUnitIncrement(20);

        frame.pack();
        frame.setVisible(true);

        frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }


    public static void plotTrajectory(ArrayTrajectory trajectory, OdeSystem odeSystem) {
        // set window class
        try {
            Toolkit xToolkit = Toolkit.getDefaultToolkit();
            java.lang.reflect.Field awtAppClassNameField =
                    xToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(xToolkit, "parasimGraph");
        } catch (final Exception e) {
            // we can ignore failed changing of window class name
        }

        XYChart chart = buildTrajectoryChart(trajectory, odeSystem, 960, 600);

        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setXAxisTickMarkSpacingHint(100);
        chart.getStyler().setToolTipsEnabled(true);
        chart.getStyler().setYAxisGroupPosition(1, Styler.YAxisPosition.Right);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        Color c = new Color(200,200,200,70);
        chart.getStyler().setLegendBackgroundColor(c);

        SwingWrapper<XYChart> l = new SwingWrapper<>(chart);
        JFrame f = l.displayChart("Time series");
        f.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }


    private static XYChart buildRobustnessGraph(Monitor monitor, int width, int height) {

        int finalSize = min(200, monitor.size()-2);

        List<Point> input = new ArrayList<>();

        for (Robustness robustness : monitor) {
            input.add(Point.of(robustness.getTime(), robustness.getValue()));
        }

        // reduce number of points
        List<Point> output = LTThreeBuckets.sorted(input, finalSize);
        float[] x = new float[finalSize+2];
        float[] y = new float[finalSize+2];
        for (int j=0; j<finalSize+2; ++j) {
            x[j] = output.get(j).getX().floatValue();
            y[j] = output.get(j).getY().floatValue();
        }

        // create Chart
        XYChart chart = new XYChartBuilder()
                        .width(width)
                        .height(height)
                        .theme(Styler.ChartTheme.Matlab)
                        .title("Robustness")
                        .xAxisTitle("Time")
                        .yAxisTitle("Robustness")
                        .build();

        // split long property name into multiple smaller parts
        String[] property = monitor.getProperty().toString().split("(?<=\\G.{40})");
        XYSeries series = chart.addSeries(String.join("\n", property), x, y);

        series.setMarker(SeriesMarkers.NONE);

        return chart;
    }


    private static XYChart buildTrajectoryChart(ArrayTrajectory trajectory,
                                                OdeSystem odeSystem,
                                                int width,
                                                int height) {
        // create Chart
        XYChart chart = new XYChartBuilder()
                        .width(width)
                        .height(height)
                        .theme(Styler.ChartTheme.Matlab)
                        .title("Time series")
                        .xAxisTitle("Time")
                        .yAxisTitle("Concentrations")
                        .build();

        int finalSize = min(200, trajectory.getLength()-2);

        for (int dimension=0; dimension<trajectory.getVarDimension(); ++dimension) {
            List<Point> input = new ArrayList<>();
            for (int i=0; i<trajectory.getLength(); ++i) {
                input.add(
                        Point.of(trajectory.getTimes()[i],
                                 trajectory.getPoints()[(i*trajectory.getVarDimension())+dimension]));
            }

            List<Point> output = LTThreeBuckets.sorted(input, finalSize);
            float[] x = new float[finalSize+2];
            float[] y = new float[finalSize+2];
            for (int j=0; j<finalSize+2; ++j) {
                x[j] = output.get(j).getX().floatValue();
                y[j] = output.get(j).getY().floatValue();
            }

            XYSeries series = chart.addSeries(odeSystem.getVariable(dimension).getName(), x, y);
            series.setMarker(SeriesMarkers.NONE);
        }

        return chart;
    }

}
