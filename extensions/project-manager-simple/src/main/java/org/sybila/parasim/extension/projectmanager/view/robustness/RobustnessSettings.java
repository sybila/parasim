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
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.sybila.parasim.extension.projectmanager.model.OdeSystemNames;
import org.sybila.parasim.extension.projectmanager.model.SimpleNamedInitialSampling;
import org.sybila.parasim.extension.projectmanager.model.SimpleNamedOrthogonalSpace;
import org.sybila.parasim.extension.projectmanager.view.CommitFormattedTextField;
import org.sybila.parasim.extension.projectmanager.view.OdeSystemFactory;
import org.sybila.parasim.extension.projectmanager.view.TableConstraints;
import org.sybila.parasim.extension.projectmanager.view.ValueHolder;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class RobustnessSettings extends JPanel implements ValueHolder<RobustnessSettingsValues> {

    private static GridBagConstraints getConstraints() {
        GridBagConstraints result = new GridBagConstraints();
        result.fill = GridBagConstraints.BOTH;
        return result;
    }

    private class Row {

        private JLabel rowLabel;
        private CommitFormattedTextField min, max;
        private JSpinner samples;
        private SpinnerNumberModel samplesModel;

        public Row(String name) {
            rowLabel = TableConstraints.getRowLabel(name);
            min = new CommitFormattedTextField(DecimalFormat.getNumberInstance());
            max = new CommitFormattedTextField(DecimalFormat.getNumberInstance());
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
            samplesModel = new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1);
            samplesModel.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent ce) {
                    fireChanges();
                }
            });
            samples = new JSpinner(samplesModel);
        }

        public void setVisible(boolean visibility) {
            rowLabel.setVisible(visibility);
            min.setVisible(visibility);
            max.setVisible(visibility);
            samples.setVisible(visibility);
        }

        public boolean isVisible() {
            return rowLabel.isVisible();
        }

        public void add(Container container, int y) {
            container.add(rowLabel, TableConstraints.getRowConstraints(y));
            container.add(samples, TableConstraints.getCellConstraints(1, y));
            container.add(min, TableConstraints.getCellConstraints(2, y));
            container.add(max, TableConstraints.getCellConstraints(3, y));
        }

        public float getMin() {
            return ((Number) min.getValue()).floatValue();
        }

        public float getMax() {
            return ((Number) max.getValue()).floatValue();
        }

        public int getSamples() {
            return samplesModel.getNumber().intValue();
        }

        public void setValues(float min, float max, int samples) {
            this.min.setValue(min);
            this.max.setValue(max);
            this.samplesModel.setValue(samples);
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
            System.out.println(ae.getSource());
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
        if (robustnessModel == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        model = robustnessModel;
        if (odeNames == null) {
            throw new IllegalArgumentException("Argument (ode names) is null.");
        }
        names = odeNames;

        filterUnvalued(names.getVariables());
        filterUnvalued(names.getParameters());

        perturbationTable = new JPanel(new GridBagLayout());
        perturbationTable.add(TableConstraints.getHeaderLabelWithToolTip("Samples", "Number of Samples"), TableConstraints.getHeaderConstraints(1));
        perturbationTable.add(TableConstraints.getHeaderLabelWithToolTip("Minimum", "Sampling Interval Minimum"), TableConstraints.getHeaderConstraints(2));
        perturbationTable.add(TableConstraints.getHeaderLabelWithToolTip("Maximum", "Sampling Interval Maximum"), TableConstraints.getHeaderConstraints(3));
        int y = 1;
        for (String name : odeNames.getVariables()) {
            addRow(name, y);
            y++;
        }
        for (String name : odeNames.getParameters()) {
            addRow(name, y);
            y++;
        }

        variables = new AnalysisChoiceBox(names.getVariables());
        parameters = new AnalysisChoiceBox(names.getParameters());

        variables.setBorder(new TitledBorder("Variables"));
        parameters.setBorder(new TitledBorder("Parameters"));

        setLayout(new GridBagLayout());

        GridBagConstraints constraints = getConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(perturbationTable, constraints);

        constraints = getConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        add(variables, constraints);

        constraints = getConstraints();
        constraints.gridx = 1;
        constraints.gridy = 1;
        add(parameters, constraints);

        setValues(names.getDefaultRobustnessSettingsValues());
    }

    private void addRow(String name, int index) {
        Row newRow = new Row(name);
        newRow.add(perturbationTable, index);
        rows.put(name, newRow);
        newRow.setVisible(unvalued.contains(name));
        if (!unvalued.contains(name)) {
            float val = names.getValue(name);
            newRow.setValues(val, val, 1);
        }
    }

    private void fireChanges() {
        model.valuesChanged(getValues());
    }

    @Override
    public RobustnessSettingsValues getValues() {
        Map<String, Integer> sampling = new HashMap<>();
        Map<String, Pair<Float, Float>> space = new HashMap<>();
        for (Map.Entry<String, Row> row : rows.entrySet()) {
            if (row.getValue().isVisible()) {
                sampling.put(row.getKey(), row.getValue().getSamples());
                space.put(row.getKey(), new Pair(row.getValue().getMin(), row.getValue().getMax()));
            }
        }
        return new RobustnessSettingsValues(new SimpleNamedInitialSampling(sampling), new SimpleNamedOrthogonalSpace(space));
    }

    private boolean setValues(String name, RobustnessSettingsValues values) {
        Pair<Float, Float> bounds = values.getInitialSpace().getValues(name);
        int samples = values.getInitialSampling().getSamples(name);
        Row row = rows.get(name);
        if (bounds != null) {
            row.setValues(bounds.first(), bounds.second(), samples);
            row.setVisible(true);
            return true;
        } else {
            if (unvalued.contains(name)) {
                throw new IllegalArgumentException("New values do not cover all parameters withou value.");
            }
            row.setVisible(false);
            return false;
        }
    }

    @Override
    public final void setValues(RobustnessSettingsValues newValues) {
        for (String name : names.getVariables()) {
            variables.setChecked(name, setValues(name, newValues));
        }
        for (String name : names.getParameters()) {
            parameters.setChecked(name, setValues(name, newValues));
        }
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
        Map<String, Integer> sampling = new HashMap<>();
        Map<String, Pair<Float, Float>> space = new HashMap<>();

        sampling.put("S", 1);
        sampling.put("I", 1);
        sampling.put("R", 1);

        space.put("S", new Pair<>(95f, 95f));
        space.put("I", new Pair<>(5f, 5f));
        space.put("R", new Pair<>(0f, 0f));

        return new RobustnessSettingsValues(new SimpleNamedInitialSampling(sampling), new SimpleNamedOrthogonalSpace(space));
    }
}
