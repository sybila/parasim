package org.sybila.parasim.extension.projectmanager.view.formulae;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface FormulaeListModel {

    public String add();

    public boolean remove(String name);

    public boolean rename(String name, String newName);

    public boolean choose(String name);
}
