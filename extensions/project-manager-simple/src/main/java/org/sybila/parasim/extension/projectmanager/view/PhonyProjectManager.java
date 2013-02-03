/**
 * Copyright 2011 - 2013, Sybila, Systems Biology Laboratory and individual
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
package org.sybila.parasim.extension.projectmanager.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import org.sybila.parasim.extension.projectmanager.api.ExperimentListener;
import org.sybila.parasim.extension.projectmanager.api.ProjectManager;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 * @deprecated
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
            JOptionPane.showMessageDialog(getRootPane(), "This project manager roughly (as a proof of concept) shows how a project manager should look like. However, it lacks all functionality.",
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
        setSize(1000, 500);
        setLocation(200, 100);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS));
        getContentPane().add(center, BorderLayout.CENTER);

        JPanel panel = createExperimentPanel();
        panel.setBorder(new LineBorder(Color.RED, 2));
        center.add(panel);

        panel = createSimulationPanel();
        panel.setBorder(new LineBorder(Color.BLUE, 2));
        center.add(panel);

        panel = createPerturbationPanel();
        panel.setBorder(new LineBorder(Color.GREEN, 2));
        center.add(panel);

        getContentPane().add(createExperimentManager(), BorderLayout.PAGE_START);
    }

    private JPanel createExperimentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JTextArea annot = new JTextArea("This is a testing experiment.");
        annot.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(annot);

        Object[][] data = new Object[][]{
            {"Timeout", "480000"},
            {"Iteration limit", "10"},
            {"Result file", "result.xml"}};
        JTable table = new JTable(data, new Object[]{"", ""});
        table.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(table);

        panel.add(createFormulaePanel());


        return panel;
    }

    private JPanel createFormulaePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JList(new Object[]{"Formula 1", "Formula 2", "Formula 3"}), BorderLayout.CENTER);
        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.PAGE_AXIS));
        controls.add(new JButton("Import"));
        controls.add(new JButton("Remove"));
        panel.add(controls, BorderLayout.LINE_END);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return panel;
    }

    private JPanel createSimulationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        Object[] columns = new Object[]{"max relative error", "time step", "simulation start", "simulation end"};
        Object[][] data = new Object[][]{{0.1, 0.1, 0, 10}};
        JScrollPane pane = new JScrollPane(new JTable(data, columns));
        pane.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(pane);

        columns = new Object[]{"Variable", "max absolute error", "min value", "max value"};
        data = new Object[][]{{"A", 0, 0, 200}, {"B", 0, 0, 200}};
        pane = new JScrollPane(new JTable(data, columns));
        pane.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.add(pane);

        panel.add(createManagerPanel());
        return panel;
    }

    private static class PerturbationTable extends JTable {

        public PerturbationTable(Object[][] data, Object[] columns) {
            super(new DefaultTableModel(data, columns));
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return String.class;
                case 1:
                    return Boolean.class;
                default:
                    return Boolean.class;
            }
        }
    }

    private JPanel createPerturbationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.LINE_AXIS));

        Object[][] data = new Object[][]{{"A", true}, {"B", false}};
        Object[] headers = new Object[]{"Variable", "Perturbed"};
        JScrollPane pane = new JScrollPane(new PerturbationTable(data, headers));
        pane.setBorder(new EmptyBorder(5, 5, 5, 5));
        center.add(pane);

        data = new Object[][]{{"k1", false}, {"k2", true}};
        headers = new Object[]{"Parameter", "Perturbed"};
        pane = new JScrollPane(new PerturbationTable(data, headers));
        pane.setBorder(new EmptyBorder(5, 5, 5, 5));
        center.add(pane);

        data = new Object[][]{{"A", 0, 20, 10}, {"k1", 0.01, 0.1, 30}};
        headers = new Object[]{"Object", "min", "max", "samples"};
        pane = new JScrollPane(new JTable(data, headers));
        pane.setBorder(new EmptyBorder(5, 5, 5, 5));

        panel.add(pane);
        panel.add(center);
        panel.add(createManagerPanel());
        return panel;
    }

    private JPanel createManagerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(new JComboBox(new Object[]{"OBJ1", "OBJ2", "OBJ3"}));
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.LINE_AXIS));
        row.add(new JButton("New"));
        row.add(new JButton("Save/Rename"));
        row.add(new JButton("Remove"));
        panel.add(row);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        return panel;
    }

    private JPanel createExperimentManager() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.add(createManagerPanel());
        panel.add(new JButton("Launch"));
        panel.add(new JButton("Show results"));
        return panel;
    }
}
