package org.sybila.parasim.extension.projectManager.view.names;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface NameManagerModel {

    public void selectionChanged(String name);

    public void newName();

    public boolean renameCurrent(String name, String newName);

    public boolean saveCurrent(String name);

    public boolean removeCurrent(String name);
}
