package org.sybila.parasim.extension.projectManager.view.formulae;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface FormulaeListModel {

    public String add();

    public void remove(String name);

    public boolean rename(String name, String newName);

    public void choose(String name);
}
