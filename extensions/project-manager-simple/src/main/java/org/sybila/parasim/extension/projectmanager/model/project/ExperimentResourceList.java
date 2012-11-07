package org.sybila.parasim.extension.projectmanager.model.project;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ExperimentResourceList<T> {

    public ResourceAction add(String name, T target);

    public T get(String name);

    public void remove(String name);

    public ResourceAction rename(String name, String newName);

    public void put(String name, T target);

    public boolean isUsedInExperiment(String name);
}
