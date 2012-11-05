package org.sybila.parasim.extension.projectManager.model.components;

import javax.swing.JOptionPane;
import org.sybila.parasim.extension.projectManager.model.project.Project;
import org.sybila.parasim.extension.projectManager.view.formulae.FormulaeListModel;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public class FormulaModel implements FormulaeListModel {

    private final Project project;

    public FormulaModel(Project targetProject) {
        if (targetProject == null) {
            throw new IllegalArgumentException("Argument (project) is null.");
        }
        project = targetProject;
    }

    private void checkName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument (name) is null.");
        }
    }

    @Override
    public boolean choose(String name) {
        checkName(name);
        // uživateli musí být jasné, že tím znevalidní model
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean remove(String name) {
        if (project.getFormulae().isUsedInExperiment(name) && JOptionPane.NO_OPTION
                == JOptionPane.showConfirmDialog(null, "This formula is used in one or more experiments."
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
            JOptionPane.showMessageDialog(null, "Unable to rename formula `" + name + "' to `" + newName + "'.", "Rename Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public String add() {
        /*
         * tohle je nejsložitější, protože tady proběhne import
         *
         * je třeba udělat komponentu, která má v sobě fileChooser a název -- ta vrátí obojí
         */
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
