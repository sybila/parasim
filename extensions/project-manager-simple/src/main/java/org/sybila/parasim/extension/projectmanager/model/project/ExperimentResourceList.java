package org.sybila.parasim.extension.projectmanager.model.project;

import java.util.Set;

/**
 *
 * @author <a href="mailto:xvejpust@fi.muni.cz">Tomáš Vejpustek</a>
 */
public interface ExperimentResourceList<T> {

    public Set<String> getNames();

    public ResourceAction add(String name, T target);

    public T get(String name);

    public void remove(String name);

    public ResourceAction rename(String name, String newName);

    public void put(String name, T target);

    public boolean isUsedInExperiment(String name);
}
