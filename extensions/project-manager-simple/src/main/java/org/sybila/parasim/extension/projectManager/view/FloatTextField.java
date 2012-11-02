package org.sybila.parasim.extension.projectManager.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FloatTextField extends CommitFormattedTextField {

    public static interface Model {

        public void putValue(String name, float value, FloatTextField target);
    }
    private Model model;
    private String name;

    public FloatTextField(String name, Model model) {
        super(DecimalFormat.getNumberInstance());
        if (model == null) {
            throw new IllegalArgumentException("Argument (model) is null.");
        }
        this.model = model;
        this.name = name;
        addCommitListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Number value = (Number) getValue();
                FloatTextField.this.model.putValue(FloatTextField.this.name, value.floatValue(), FloatTextField.this);
            }
        });
    }
}
