package org.sybila.parasim.extension.projectmanager.view.experiment;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.sybila.parasim.extension.projectmanager.view.CommitFormattedTextField;
import org.sybila.parasim.extension.projectmanager.view.OdeSystemFactory;
import org.sybila.parasim.extension.projectmanager.view.TableConstraints;
import org.sybila.parasim.extension.projectmanager.view.names.NameChooser;
import org.sybila.parasim.extension.projectmanager.view.names.NameChooserModel;
import org.sybila.parasim.extension.projectmanager.view.names.NameList;
import org.sybila.parasim.model.ode.OdeSystem;
import org.sybila.parasim.util.Pair;
import org.sybila.parasim.util.SimpleLock;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class ExperimentSettings extends JPanel {

    private GridBagConstraints getConstraints() {
        GridBagConstraints result = new GridBagConstraints();
        result.fill = GridBagConstraints.BOTH;
        return result;
    }

    private GridBagConstraints getLeftConstraints(int y) {
        GridBagConstraints result = getConstraints();
        result.gridx = 0;
        result.gridy = y;
        result.insets = new Insets(1, 1, 1, 2);
        return result;
    }

    private GridBagConstraints getRightConstraints(int y) {
        GridBagConstraints result = getConstraints();
        result.gridx = 1;
        result.gridy = y;
        result.weightx = 0.5;
        result.insets = new Insets(1, 2, 1, 1);
        return result;
    }
    //
    private ExperimentSettingsModel model;
    private SimpleLock lock = new SimpleLock();
    //
    private NameChooser formulae, simulation, robustness;
    private CommitFormattedTextField iterationField, timeoutField;
    private JTextArea annotation;

    public ExperimentSettings(ExperimentSettingsModel experimentModel, Set<String> formulaeNames, Set<String> simulationsNames, Set<String> robustnessNames) {
        if (experimentModel == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        model = experimentModel;

        iterationField = new CommitFormattedTextField(new Integer(0));
        iterationField.addCommitListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                int value = ((Number) iterationField.getValue()).intValue();
                if (value < 0) {
                    iterationField.setValue(0);
                }
                fireChanges();
            }
        });
        timeoutField = new CommitFormattedTextField(new Long(0));
        timeoutField.addCommitListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                long value = ((Number) timeoutField.getValue()).longValue();
                if (value < 0) {
                    timeoutField.setValue(0);
                }
                fireChanges();

            }
        });

        setLayout(new GridBagLayout());
        add(TableConstraints.getRowLabel("Timeout"), getLeftConstraints(0));
        add(TableConstraints.getRowLabel("Iteration limit"), getLeftConstraints(1));
        add(TableConstraints.getRowLabel("Formula"), getLeftConstraints(2));
        add(TableConstraints.getRowLabel("Simulation settings"), getLeftConstraints(3));
        add(TableConstraints.getRowLabel("Robustness settings"), getLeftConstraints(4));

        add(timeoutField, getRightConstraints(0));
        add(iterationField, getRightConstraints(1));
        formulae = new NameChooser(model.getFormulaChooser(), formulaeNames);
        add(formulae, getRightConstraints(2));
        simulation = new NameChooser(model.getSimulationChooser(), simulationsNames);
        add(simulation, getRightConstraints(3));
        robustness = new NameChooser(model.getRobustnessChooser(), robustnessNames);
        add(robustness, getRightConstraints(4));

        annotation = new JTextArea(5, 35);
        annotation.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                if (lock.isAccessible()) {
                    model.annotationChanged(getAnnotation());
                }
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                if (lock.isAccessible()) {
                    model.annotationChanged(getAnnotation());
                }
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                if (lock.isAccessible()) {
                    model.annotationChanged(getAnnotation());
                }
            }
        });
        annotation.setLineWrap(true);
        annotation.setWrapStyleWord(true);
        GridBagConstraints annotationConstraints = getConstraints();
        annotationConstraints.gridy = 5;
        annotationConstraints.gridwidth = 2;
        annotationConstraints.weightx = 1;
        annotationConstraints.weighty = 1;
        add(annotation, annotationConstraints);
    }

    private void fireChanges() {
        if (lock.isAccessible()) {
            model.valuesChanged(getValues().first());
        }
    }

    public NameList getFormulaeNameList() {
        return formulae;
    }

    public NameList getSimulationsNameList() {
        return simulation;
    }

    public NameList getRobustnessNameList() {
        return robustness;
    }

    public String getAnnotation() {
        return annotation.getText();
    }

    public Pair<ExperimentSettingsValues, String> getValues() {
        Number iteration = (Number) iterationField.getValue();
        Number timeout = (Number) timeoutField.getValue();
        return new Pair(new ExperimentSettingsValues(iteration.intValue(), timeout.longValue()), getAnnotation());
    }

    public void setValues(Pair<ExperimentSettingsValues, String> target) {
        lock.lock();
        annotation.setText(target.second());
        timeoutField.setValue(target.first().getTimeout());
        iterationField.setValue(target.first().getIterationLimit());
        lock.unlock();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                OdeSystem system = OdeSystemFactory.getInstance().getTestingOdeSystem();
                JFrame frame = new JFrame();
                frame.setSize(750, 500);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ExperimentSettings settings = new ExperimentSettings(new ExperimentSettingsModel() {

                    @Override
                    public NameChooserModel getFormulaChooser() {
                        return new NameChooserModel() {

                            @Override
                            public void chooseName(String name) {
                                System.out.println("Formula `" + name + "' chosen.");
                            }

                            @Override
                            public void seeName(String name) {
                                System.out.println("Formula `" + name + "' referenced.");
                            }
                        };
                    }

                    @Override
                    public NameChooserModel getRobustnessChooser() {
                        return new NameChooserModel() {

                            @Override
                            public void chooseName(String name) {
                                System.out.println("Robustness settings `" + name + "' chosen.");
                            }

                            @Override
                            public void seeName(String name) {
                                System.out.println("Robustness settings `" + name + "' referenced.");
                            }
                        };
                    }

                    @Override
                    public NameChooserModel getSimulationChooser() {
                        return new NameChooserModel() {

                            @Override
                            public void chooseName(String name) {
                                System.out.println("Simulation settings `" + name + "' chosen.");
                            }

                            @Override
                            public void seeName(String name) {
                                System.out.println("Simulation settings `" + name + "' referenced.");
                            }
                        };
                    }

                    @Override
                    public void valuesChanged(ExperimentSettingsValues values) {
                        System.out.println("Values changed.");
                    }

                    @Override
                    public void annotationChanged(String annotation) {
                        System.out.println("Annotation changed to `" + annotation + "'.");
                    }
                }, Collections.<String>emptySet(), Collections.<String>emptySet(), Collections.<String>emptySet());
                frame.add(settings);
                frame.setVisible(true);
            }
        });
    }
}