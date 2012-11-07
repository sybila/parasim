package org.sybila.parasim.extension.projectManager.model.project;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ResourceAction {

    public boolean isViable();

    public void commit();

    public void revert();
}
