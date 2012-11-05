package org.sybila.parasim.extension.projectManager.model.project;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ExperimentResourceList<T> extends ResourceList<T> {

    public void put(String name, T target);

    public boolean isUsedInExperiment(String name);

    public boolean duplicate(String name, String newName);
}
