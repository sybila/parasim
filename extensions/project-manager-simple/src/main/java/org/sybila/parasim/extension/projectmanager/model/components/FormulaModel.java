/**
 * Copyright 2011 - 2012, Sybila, Systems Biology Laboratory and individual
 * contributors by the @authors tag.
 *
 * This file is part of Parasim.
 *
 * Parasim is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.sybila.parasim.extension.projectmanager.model.components;

import java.io.File;
import javax.swing.JOptionPane;
import org.sybila.parasim.extension.projectmanager.model.project.Project;
import org.sybila.parasim.extension.projectmanager.view.FormulaImporter;
import org.sybila.parasim.extension.projectmanager.view.formulae.FormulaeListModel;
import org.sybila.parasim.util.Pair;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FormulaModel implements FormulaeListModel {

    private final Project project;
    private final ExperimentModel experiment;
    private final FormulaImporter importer = new FormulaImporter();

    public FormulaModel(Project targetProject, ExperimentModel experimentModel) {
        if (targetProject == null) {
            throw new IllegalArgumentException("Argument (project) is null.");
        }
        if (experimentModel == null) {
            throw new IllegalArgumentException("Argument (experiment model) is null.");
        }
        project = targetProject;
        experiment = experimentModel;
    }

    private void checkName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument (name) is null.");
        }
    }

    @Override
    public boolean choose(String name) {
        checkName(name);
        experiment.getFormulaChooser().chooseName(name);
        return true;
    }

    @Override
    public boolean remove(String name) {
        if (project.getFormulae().isUsedInExperiment(name) && JOptionPane.NO_OPTION
                == JOptionPane.showConfirmDialog(null, "This property is used in one or more experiments."
                + " Do you really want to delete it?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
            return false;
        }

        project.getFormulae().remove(name);
        return true;
    }

    @Override
    public boolean rename(String name, String newName) {
        checkName(name);
        if (!project.getFormulae().rename(name, newName)) {
            JOptionPane.showMessageDialog(null, "Unable to rename property `" + name + "' to `" + newName + "'.", "Rename Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public String add() {
        Pair<File, String> result = importer.showDialog();
        if (result == null) {
            return null;
        }

        if (project.getFormulae().add(result.second(), result.first())) {
            return result.second();
        }
        JOptionPane.showMessageDialog(null, "Unable to import property `" + result.second() + "' from `" + result.first().toString() + "'.", "Formula Import Error", JOptionPane.ERROR_MESSAGE);
        return null;
    }

    @Override
    public String getFormula(String name) {
        return project.getFormulae().getFormulaDescription(name);
    }
}
