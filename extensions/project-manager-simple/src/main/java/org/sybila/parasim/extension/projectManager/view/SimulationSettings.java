package org.sybila.parasim.extension.projectManager.view;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.sybila.parasim.computation.simulation.api.ArrayPrecisionConfiguration;
import org.sybila.parasim.computation.simulation.api.PrecisionConfiguration;
import org.sybila.parasim.extension.projectManager.model.OdeSystemNames;
import org.sybila.parasim.extension.projectManager.model.SimpleNamedOrthogonalSpace;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class SimulationSettings extends JPanel {

    private GridBagConstraints getDefaultConstraints() {
        GridBagConstraints result = new GridBagConstraints();
        result.fill = GridBagConstraints.BOTH;
        result.insets = new Insets(1, 3, 1, 1);
        return result;
    }

    private GridBagConstraints getLeftConstraints(int y) {
        GridBagConstraints result = getDefaultConstraints();
        result.gridx = 0;
        result.gridy = y;
        return result;
    }

    private GridBagConstraints getRightConstraints(int y) {
        GridBagConstraints result = getDefaultConstraints();
        result.gridx = 1;
        result.gridy = y;
        return result;
    }

    private JLabel getLabel(String name) {
        JLabel result = new JLabel(name);
        result.setHorizontalAlignment(JLabel.RIGHT);
        return result;
    }

    private void configureTextField(JFormattedTextField target) {
        target.setColumns(7);
    }

    private class VariablePanel extends JPanel {

        private GridBagConstraints getConstraints() {
            GridBagConstraints result = new GridBagConstraints();
            result.weightx = 1;
            result.weighty = 1;
            result.fill = GridBagConstraints.BOTH;
            result.insets = new Insets(1, 2, 1, 2);
            result.ipadx = 5;
            return result;
        }

        private GridBagConstraints getRowConstraints(int y) {
            GridBagConstraints result = getConstraints();
            result.gridx = 0;
            result.gridy = y;
            return result;
        }

        private GridBagConstraints getCellConstraints(int x, int y) {
            GridBagConstraints result = getConstraints();
            result.gridx = x;
            result.gridy = y;
            return result;
        }

        private GridBagConstraints getHeaderConstraints(int x) {
            GridBagConstraints result = getConstraints();
            result.gridx = x;
            result.gridy = 0;
            return result;
        }

        private JLabel getHeaderLabel(String name) {
            JLabel result = new JLabel(name);
            result.setHorizontalAlignment(JLabel.CENTER);
            return result;
        }

        private JLabel getRowLabel(String name) {
            JLabel result = new JLabel(name);
            result.setHorizontalAlignment(JLabel.RIGHT);
            return result;
        }
        //
        private Map<String, JFormattedTextField[]> fields = new HashMap<>();

        public VariablePanel() {
            setLayout(new GridBagLayout());
            //labels
            add(getRowLabel("Maximum absolute error"), getRowConstraints(1));
            add(getRowLabel("Minimum bound"), getRowConstraints(2));
            add(getRowLabel("Maximum bound"), getRowConstraints(3));
            int x = 1;
            for (String var : names.getVariables()) {
                add(getHeaderLabel(var), getHeaderConstraints(x));
                JFormattedTextField[] field = new JFormattedTextField[3];
                field[0] = new FloatTextField(var, new FloatTextField.Model() {

                    @Override
                    public void putValue(String name, float value, JFormattedTextField field) {
                        if (value < 0) {
                            value = 0;
                            field.setValue(value);
                        }
                        absoluteErrors.put(name, value);
                        fireChange();
                    }
                });
                configureTextField(field[0]);
                field[1] = new FloatTextField(var, new FloatTextField.Model() {

                    @Override
                    public void putValue(String name, float value, JFormattedTextField field) {
                        Pair<Float, Float> bound = bounds.get(name);
                        if (value >= bound.second()) {
                            value = bound.second();
                            field.setValue(value);
                        }
                        bounds.put(name, new Pair<>(value, bound.second()));
                        fireChange();
                    }
                });
                configureTextField(field[1]);
                field[2] = new FloatTextField(var, new FloatTextField.Model() {

                    @Override
                    public void putValue(String name, float value, JFormattedTextField field) {
                        Pair<Float, Float> bound = bounds.get(name);
                        if (value <= bound.first()) {
                            value = bound.first();
                            field.setValue(value);
                        }
                        bounds.put(name, new Pair<>(bound.first(), value));
                        fireChange();
                    }
                });
                configureTextField(field[2]);

                fields.put(var, field);

                add(field[0], getCellConstraints(x, 1));
                add(field[1], getCellConstraints(x, 2));
                add(field[2], getCellConstraints(x, 3));

                x++;
            }
        }

        public void reloadFields() {
            for (String name : names.getVariables()) {
                JFormattedTextField[] field = fields.get(name);
                field[0].setValue(absoluteErrors.get(name));
                field[1].setValue(bounds.get(name).first());
                field[2].setValue(bounds.get(name).second());
            }
        }
    }
    private SimulationSettingsModel model;
    private Map<String, Pair<Float, Float>> bounds;
    private Map<String, Float> absoluteErrors = new HashMap<>();
    private float relativeError, timeStep, timeStart, timeEnd;
    private OdeSystemNames names = null;
    //
    private VariablePanel variablePanel = null;
    private FloatTextField relativeErrorField, timeStepField, timeStartField, timeEndField;

    public SimulationSettings(SimulationSettingsModel settingsModel, OdeSystemNames odeNames) {
        if (settingsModel == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        if (odeNames == null) {
            throw new IllegalArgumentException("Argument (names) is null.");
        }
        model = settingsModel;
        names = odeNames;

        setLayout(new GridBagLayout());

        add(getLabel("Simulation start"), getLeftConstraints(0));
        add(timeStartField = new FloatTextField(null, new FloatTextField.Model() {

            @Override
            public void putValue(String name, float value, JFormattedTextField target) {
                if (value > timeEnd) {
                    value = timeEnd;
                    target.setValue(value);
                } else if (value < 0) {
                    value = 0;
                    target.setValue(value);
                }
                timeStart = value;
                fireChange();
            }
        }), getRightConstraints(0));
        add(getLabel("Simulation end"), getLeftConstraints(1));
        add(timeEndField = new FloatTextField(null, new FloatTextField.Model() {

            @Override
            public void putValue(String name, float value, JFormattedTextField target) {
                if (value < timeStart) {
                    value = timeStart;
                    target.setValue(value);
                }
                timeEnd = value;
                fireChange();
            }
        }), getRightConstraints(1));
        add(getLabel("Time step"), getLeftConstraints(2));
        add(timeStepField = new FloatTextField(null, new FloatTextField.Model() {

            @Override
            public void putValue(String name, float value, JFormattedTextField target) {
                if (value < 0) {
                    value = 0;
                    target.setValue(value);
                }
                timeStep = value;
                fireChange();
            }
        }), getRightConstraints(2));
        add(getLabel("Maximum relative error"), getLeftConstraints(3));
        add(relativeErrorField = new FloatTextField(null, new FloatTextField.Model() {

            @Override
            public void putValue(String name, float value, JFormattedTextField target) {
                if (value < 0) {
                    value = 0;
                    target.setValue(value);
                }
                relativeError = value;
                fireChange();
            }
        }), getRightConstraints(3));

        variablePanel = new VariablePanel();
        GridBagConstraints constraints = getDefaultConstraints();
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        constraints.gridy = 4;
        add(variablePanel, constraints);
    }

    private void fireChange() {
        model.valuesChanged(getValues());
    }

    public SimulationSettingsValues getValues() {
        if (names == null) {
            throw new IllegalStateException("No ode system set.");
        }
        float[] errors = new float[names.getVariables().size()];
        for (String variable : names.getVariables()) {
            Float error = absoluteErrors.get(variable);
            if (error == null) {
                throw new IllegalStateException();
            }
            errors[names.getDimension(variable)] = error;
        }
        PrecisionConfiguration precision = new ArrayPrecisionConfiguration(errors, relativeError, timeStep);
        return new SimulationSettingsValues(precision, new SimpleNamedOrthogonalSpace(bounds), timeStart, timeEnd);
    }

    public void setValues(SimulationSettingsValues values) {
        int size = names.getVariables().size();
        if (values.getPrecisionConfiguration().getDimension() != size) {
            throw new IllegalArgumentException("Precision configuration has wrong dimension.");
        }
        if (values.getSimulationSpace().getVariables().size() != size) {
            throw new IllegalArgumentException("Simulation space has wrong dimension.");
        }

        Map<String, Pair<Float, Float>> newBounds = new HashMap<>();
        for (String name : names.getVariables()) {
            Pair<Float, Float> bound = values.getSimulationSpace().getValues(name);
            if (bound == null) {
                throw new IllegalArgumentException("Argument (simulation space) does not contain one or more variables.");
            }
            newBounds.put(name, bound);
        }

        absoluteErrors.clear();
        for (int i = 0; i < size; i++) {
            absoluteErrors.put(names.getVariable(i), values.getPrecisionConfiguration().getMaxAbsoluteError(i));
        }
        bounds = newBounds;
        timeStep = values.getPrecisionConfiguration().getTimeStep();
        relativeError = values.getPrecisionConfiguration().getMaxRelativeError();
        timeStart = values.getSimulationStart();
        timeEnd = values.getSimulationEnd();

        variablePanel.reloadFields();
        timeStartField.setValue(timeStart);
        timeEndField.setValue(timeEnd);
        timeStepField.setValue(timeStep);
        relativeErrorField.setValue(relativeError);

        fireChange();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                OdeSystem system = OdeSystemFactory.getInstance().getTestingOdeSystem();
                JFrame frame = new JFrame();
                frame.setSize(350, 500);
                SimulationSettings settings = new SimulationSettings(new SimulationSettingsModel() {

                    @Override
                    public void valuesChanged(SimulationSettingsValues values) {
                        System.out.println("Change fired.");
                    }
                }, new OdeSystemNames(system));
                settings.setValues(testValues());
                frame.add(settings);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            }
        });
    }

    private static SimulationSettingsValues testValues() {
        PrecisionConfiguration precision = new ArrayPrecisionConfiguration(new float[]{0f, 0f, 0f}, 0.1f, 0.1f);
        Map<String, Pair<Float, Float>> bounds = new HashMap<>();
        bounds.put("S", new Pair<>(0f, 100f));
        bounds.put("I", new Pair<>(0f, 100f));
        bounds.put("R", new Pair<>(0f, 100f));
        return new SimulationSettingsValues(precision, new SimpleNamedOrthogonalSpace(bounds), 0, 100);
    }
}