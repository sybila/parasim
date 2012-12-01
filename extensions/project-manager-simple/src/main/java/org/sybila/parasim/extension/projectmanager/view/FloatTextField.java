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
package org.sybila.parasim.extension.projectmanager.view;

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
        super(new DecimalFormat("0.####E0"));
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
