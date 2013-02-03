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
package org.sybila.parasim.extension.projectmanager.view.robustness;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import org.apache.commons.lang3.Validate;
import org.sybila.parasim.extension.projectmanager.model.OdeSystemNames;
import org.sybila.parasim.extension.projectmanager.model.SimpleNamedOrthogonalSpace;
import org.sybila.parasim.extension.projectmanager.view.CommitFormattedTextField;
import org.sybila.parasim.extension.projectmanager.view.OdeSystemFactory;
import org.sybila.parasim.extension.projectmanager.view.ScrollPane;
import org.sybila.parasim.extension.projectmanager.view.TableConstraints;
import org.sybila.parasim.extension.projectmanager.view.ValueHolder;
import org.sybila.parasim.util.Pair;
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class RobustnessSettings extends JPanel implements ValueHolder<RobustnessSettingsValues> {

    private static GridBagConstraints getConstraints() {
        GridBagConstraints result = new GridBagConstraints();
        result.fill = GridBagConstraints.BOTH;
        result.weighty = 1;
        result.weightx = 1;
        return result;
    }

    private class Row {

        private JLabel rowLabel;
        private CommitFormattedTextField min, max;

        public Row(String name) {
            rowLabel = TableConstraints.getRowLabel(name);
            DecimalFormat format = new DecimalFormat("0.####E0");
            min = new CommitFormattedTextField(format);
            max = new CommitFormattedTextField(format);
            min.addCommitListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (getMin() > getMax()) {
                        min.setValue(getMax());
                    }
                    fireChanges();
                }
            });
            max.addCommitListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (getMax() < getMin()) {
                        max.setValue(getMin());
                    }
                    fireChanges();
                }
            });
        }

        public void setVisible(boolean visibility) {
            rowLabel.setVisible(visibility);
            min.setVisible(visibility);
            max.setVisible(visibility);
        }

        public boolean isVisible() {
            return rowLabel.isVisible();
        }

        public void add(Container container, int y) {
            container.add(rowLabel, TableConstraints.getRowConstraints(y));
            container.add(min, TableConstraints.getCellConstraints(1, y));
            container.add(max, TableConstraints.getCellConstraints(2, y));
        }

        public float getMin() {
            return ((Number) min.getValue()).floatValue();
        }

        public float getMax() {
            return ((Number) max.getValue()).floatValue();
        }

        public void setValues(float min, float max) {
            this.min.setValue(min);
            this.max.setValue(max);
        }
    }

    private class ChoiceBoxListener implements ActionListener {

        private final String name;
        private final JCheckBox box;

        public ChoiceBoxListener(String name, JCheckBox box) {
            this.name = name;
            this.box = box;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            rows.get(name).setVisible(box.isSelected());
            fireChanges();
        }
    }

    private class AnalysisChoiceBox extends JPanel {

        private final Map<String, JCheckBox> boxes = new HashMap<>();

        public AnalysisChoiceBox(Set<String> vars) {
            setLayout(new GridBagLayout());
            //labels
            add(TableConstraints.getHeaderLabel("Value"), TableConstraints.getHeaderConstraints(1));
            add(TableConstraints.getHeaderLabelWithToolTip("?", "Analyze?"), TableConstraints.getHeaderConstraints(2));
            int y = 1;
            for (String var : vars) {
                add(TableConstraints.getRowLabel(var), TableConstraints.getRowConstraints(y));
                Float value = names.getValue(var);
                String val;
                JCheckBox box = new JCheckBox();
                box.setBackground(Color.WHITE);
                if (value == null) {
                    val = "N/A";
                    box.setSelected(true);
                    box.setEnabled(false);
                } else {
                    val = value.toString();
                    box.setSelected(false);
                    box.addActionListener(new ChoiceBoxListener(var, box));
                }

                add(TableConstraints.getCellLabel(val), TableConstraints.getCellConstraints(1, y));
                add(box, TableConstraints.getCellConstraints(2, y));
                boxes.put(var, box);

                y++;
            }
        }

        public void setChecked(String name, boolean checked) {
            JCheckBox box = boxes.get(name);
            if (box.isEnabled()) {
                box.setSelected(checked);
            }
        }
    }
    //
    private final SimpleLock lock = new SimpleLock();
    private RobustnessSettingsModel model;
    private OdeSystemNames names;
    private Map<String, Row> rows = new HashMap<>();
    private Set<String> unvalued = new HashSet<>();
    //
    private AnalysisChoiceBox variables, parameters;
    private JPanel perturbationTable;

    private void filterUnvalued(Set<String> objects) {
        for (String obj : objects) {
            Float value = names.getValue(obj);
            if (value == null) {
                unvalued.add(obj);
            }
        }
    }

    public RobustnessSettings(RobustnessSettingsModel robustnessModel, OdeSystemNames odeNames) {
        Validate.notNull(robustnessModel);
        Validate.notNull(odeNames);
        model = robustnessModel;
        names = odeNames;

        filterUnvalued(names.getVariables());
        filterUnvalued(names.getParameters());

        perturbationTable = new JPanel(new GridBagLayout());
        perturbationTable.add(TableConstraints.getHeaderLabelWithToolTip("Minimum", "Sampling Interval Minimum"), TableConstraints.getHeaderConstraints(1));
        perturbationTable.add(TableConstraints.getHeaderLabelWithToolTip("Maximum", "Sampling Interval Maximum"), TableConstraints.getHeaderConstraints(2));
        int y = 1;
        for (String name : odeNames.getVariables()) {
            addRow(name, y);
            y++;
        }
        for (String name : odeNames.getParameters()) {
            addRow(name, y);
            y++;
        }
        JScrollPane perturbationPane = new ScrollPane(perturbationTable);

        variables = new AnalysisChoiceBox(names.getVariables());
        parameters = new AnalysisChoiceBox(names.getParameters());
        JScrollPane variablesPane = new ScrollPane(variables);
        variablesPane.setBorder(new TitledBorder("Variables"));
        JScrollPane parametersPane = new ScrollPane(parameters);
        parametersPane.setBorder(new TitledBorder("Parameters"));

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = getConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(perturbationPane, constraints);

        constraints = getConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(variablesPane, constraints);

        constraints = getConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        add(parametersPane, constraints);

        setValues(names.getDefaultRobustnessSettingsValues());
    }

    private void addRow(String name, int index) {
        Row newRow = new Row(name);
        newRow.add(perturbationTable, index);
        rows.put(name, newRow);
        newRow.setVisible(unvalued.contains(name));
        if (!unvalued.contains(name)) {
            float val = names.getValue(name);
            newRow.setValues(val, val);
        }
    }

    private void fireChanges() {
        if (lock.isAccessible()) {
            model.valuesChanged(getValues());
        }
    }

    @Override
    public RobustnessSettingsValues getValues() {
        Map<String, Pair<Float, Float>> space = new HashMap<>();
        for (Map.Entry<String, Row> row : rows.entrySet()) {
            if (row.getValue().isVisible()) {
                space.put(row.getKey(), new Pair(row.getValue().getMin(), row.getValue().getMax()));
            }
        }
        return new RobustnessSettingsValues(new SimpleNamedOrthogonalSpace(space));
    }

    private boolean setValues(String name, RobustnessSettingsValues values) {
        lock.lock();
        Pair<Float, Float> bounds = values.getInitialSpace().getValues(name);
        Row row = rows.get(name);
        boolean nonEmpty = (bounds != null);
        if (nonEmpty) {
            row.setValues(bounds.first(), bounds.second());
        } else {
            if (unvalued.contains(name)) {
                throw new IllegalArgumentException("New values do not cover all parameters withou value.");
            }
        }
        row.setVisible(nonEmpty);
        lock.unlock();
        return nonEmpty;

    }

    @Override
    public final void setValues(RobustnessSettingsValues newValues) {
        lock.lock();
        for (String name : names.getVariables()) {
            variables.setChecked(name, setValues(name, newValues));
        }
        for (String name : names.getParameters()) {
            parameters.setChecked(name, setValues(name, newValues));
        }
        lock.unlock();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                OdeSystemNames names = new OdeSystemNames(OdeSystemFactory.getInstance().getTestingOdeSystem());
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                RobustnessSettings settings = new RobustnessSettings(new RobustnessSettingsModel() {

                    @Override
                    public void valuesChanged(RobustnessSettingsValues values) {
                        System.out.println("Change fired.");
                    }
                }, names);
                settings.setValues(getTestValues());
                frame.add(settings);
                frame.setSize(500, 750);
                frame.setVisible(true);
            }
        });
    }

    public static RobustnessSettingsValues getTestValues() {
        Map<String, Pair<Float, Float>> space = new HashMap<>();

        space.put("S", new Pair<>(95f, 95f));
        space.put("I", new Pair<>(5f, 5f));
        space.put("R", new Pair<>(0f, 0f));

        return new RobustnessSettingsValues(new SimpleNamedOrthogonalSpace(space));
    }
}
