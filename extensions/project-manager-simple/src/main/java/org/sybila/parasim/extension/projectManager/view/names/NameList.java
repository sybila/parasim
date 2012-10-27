package org.sybila.parasim.extension.projectManager.view.names;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface NameList {

    public void addName(String name);

    public void removeName(String name);

    public void renameName(String name, String newName);

    public void selectName(String name);
}
